package myJade;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.HashMap;
import java.util.Map;
import kutil.core.*;
import kutil.kobjects.KObject;
import kutil.kobjects.KObjectFactory;
import kutil.xml.XmlLoader;

/**
 *
 * @author Tomáš Křen
 */

public class KutilEnviromentAgent extends Agent {
    
    private Scheduler kutilScheduler;
    
    private Map< String , KObject > incarnations ;
    private Map< String , String  > id2localname ;
    
    
    private Scheduler mkKutilScheduler( ){
        
        Global.init();

        XmlLoader loader = new XmlLoader();
        KAtts loaded = loader.loadResource( "main-jade.xml" );

        if( loaded == null ) {
            Log.it("[XML-ERROR => nic se nenahrálo.]");
            return null;
        }
        else {
            return new Scheduler( loaded , this );
        }
    }
    
    private KObject addKObjectFromXML( String parentID , String xml ){
        
        KObject newKObject = KObjectFactory.newKObject( xml );
        KObject parent = Global.idDB().get(parentID);
        
        newKObject.setParent( parent );
        parent.add( newKObject );
        
        return newKObject ;
    }
    
    public void informAboutEnvMsgHitTarget( String from , String targetID , String msgText ){
        //Log.it( "Msg " + "'" + msgText + "' from "+ from +" hit the target " + targetID + "."  );
        
        final String msgText_ = msgText;
        final String receiverLocalName = id2localname.get(targetID);
        final String senderLocalName   = id2localname.get(from);
        
        addBehaviour( new OneShotBehaviour(this) {
            @Override
            public void action() {                
                
                ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
                msg.addReceiver(new AID( receiverLocalName , AID.ISLOCALNAME) );
                msg.setContent(  "envmsg " + senderLocalName + " " + msgText_  ); 
                
                send( msg );
                
            }
        });
        
    }
    
    @Override
    protected void setup() {
        Log.it("Adding Kutil Enviroment Agent !");
        
        // registrace v jelou pejdžis
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("kutil-enviroment");
        sd.setName("kutil-enviroment");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }        
        
        
        
        kutilScheduler = mkKutilScheduler();
        incarnations   = new HashMap<String, KObject>();
        id2localname   = new HashMap<String, String>();
        
         
        addBehaviour(new TickerBehaviour(this, 6) {
            protected void onTick() {
                // perform operation X
                kutilScheduler.krok_();
                
                ACLMessage msg = myAgent.receive() ;
                if( msg != null ){
                    String[] parts = msg.getContent().split("\\s+" , 2) ;
                    String cmdName = parts[0];
                    String rest = parts[1];
                    
                    if( "incarnate".equals(cmdName) ){
                        String incarnationXML = rest ;
                        //Log.it( "Incarnation xml :" + incarnationXML );
                        KObject incarnationKObject = addKObjectFromXML( "$universe" , incarnationXML );
                        
                        incarnations.put( msg.getSender().getLocalName() , incarnationKObject );
                        id2localname.put( incarnationKObject.id() , msg.getSender().getLocalName() );
                    
                        //Log.it("!!! : " + incarnationKObject.id() );
                        
                    } else if( "envmsg".equals(cmdName) ){
                        String[] parts_ = rest.split("\\s+" , 2) ;
                        String receiverName = parts_[0];
                        String envmsg = parts_[1];
                        
                        KObject senderIncarnation   = incarnations.get( msg.getSender().getLocalName() );
                        KObject receiverIncarnation = incarnations.get( receiverName );
                        if( senderIncarnation != null && receiverIncarnation != null ){
                            
                            Int2D missilePos  = senderIncarnation.pos().plus( new Int2D( 0 , -32 ) );
                            String targetID   = receiverIncarnation.id();
                            String missileXML = 
                                    "<object type=\"textMissile\" "+
                                            "pos=\""+ missilePos.toString() +"\" "+
                                            "val=\""+ envmsg +"\" "+
                                            "target=\"" + targetID + "\" "+
                                            "from=\""+ senderIncarnation.id() +"\"   />" ; 
                            
                            addKObjectFromXML( "$universe" , missileXML );
                            
                        }
                        
                        
                    }
                    
                    
                    
                }
                
                
                
            }
        } );
        
        
    }
}