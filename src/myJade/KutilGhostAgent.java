package myJade;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import kutil.core.Int2D;
import kutil.core.Log;
import kutil.kobjects.Fly;

/**
 *
 * @author Tomáš Křen
 */
public class KutilGhostAgent extends Agent {

    private AID kutilEnviromentAID ;
    
    private int ghostID ;
    
    private static int nextGhostID = 1;
    
    
    @Override
    protected void setup() {
        Log.it("Adding Ghost Agent !");
        
        ghostID = nextGhostID;
        nextGhostID ++;
        
        //najde ve žlutejch strankach agenta co dělá kutil-enviroment
        addBehaviour( new OneShotBehaviour(this) {
            @Override
            public void action() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("kutil-enviroment");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    if( result.length > 0 ) {
                        kutilEnviromentAID = result[0].getName();
                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });
        

        
        
    }
    
    
    public ACLMessage mkIncarnateMsg( Int2D pos ){
        
        Int2D goal = Fly.toGoalPos( pos );
        
        String xml =  
                "<object type=\"fly\" pos=\""+ pos.toString() +"\" goal=\""+ goal.toString() +"\">"+ 
                    "<object type=\"touchSensor\" id=\"$ts_"+ghostID+"\"  pos=\"0 0\"     target=\"$cw2_"+ghostID+":0\" val=\"touchSensor\" />" +
                    "<object type=\"goalSensor\"  id=\"$gs_"+ghostID+"\"  pos=\"102 0\"   target=\"$as_"+ghostID+":0\"  val=\"goalSensor\"  />" +
                    "<object type=\"function\"    id=\"$fc1_"+ghostID+"\" pos=\"2 223\"                                 val=\"flyCmd\"      />" +
                    "<object type=\"function\"    id=\"$cw1_"+ghostID+"\" pos=\"2 143\"   target=\"$fc1_"+ghostID+":0\" val=\"rotCW\"       />" +
                    "<object type=\"function\"    id=\"$cw2_"+ghostID+"\" pos=\"0 67\"    target=\"$cw1_"+ghostID+":0\" val=\"rotCW\"       />" +
                    "<object type=\"function\"    id=\"$as_"+ghostID+"\"  pos=\"104 85\"  target=\"$fc2_"+ghostID+":0\" val=\"appleSensor\" />" +
                    "<object type=\"function\"    id=\"$fc2_"+ghostID+"\" pos=\"105 166\"                               val=\"flyCmd\"      />" +
                "</object>";
        
        return mkMsgForAgentE( "incarnate " + xml );
    }
    
    public ACLMessage mkEnvmsgMsg( String receiverName , String msg ){
        return mkMsgForAgentE( "envmsg " + receiverName + " " + msg );        
    }
    
    
    
    
    private ACLMessage mkMsgForAgentE( String content ){
            
        if( kutilEnviromentAID == null ){
            return null;
        }
        
        ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
        msg.addReceiver( kutilEnviromentAID ); //new AID( "e" , AID.ISLOCALNAME) );
        //msg.setLanguage("English");                
        //msg.setOntology("kutil-stuff");
        msg.setContent( content );

        return msg ;
            
    }
    
    
}
