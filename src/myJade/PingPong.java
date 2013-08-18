package myJade;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 *
 * @author Tomáš Křen
 */
public class PingPong extends KutilGhostAgent {

    
    @Override
    protected void setup() {
        
        super.setup();
        
        // registrace v jelou pejdžis
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ping-pong");
        sd.setName("ping-pong");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        } 
        
        
        // inkarnace
        addBehaviour( new OneShotBehaviour(this) {
            @Override
            public void action() {
               
                //inkarnuje se
                send( mkIncarnateMsg( new Int2D( 100, 300) ) );                              
            
                //najde ve žlutejch strankach jineho agenta co dělá ping-pong a když tzam je tak si začne pinkat
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("ping-pong");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    if( result.length > 1 ) {
                        int i = Global.random().nextInt( result.length );

                        if( myAgent.getLocalName().equals( result[i].getName().getLocalName()  )  ){
                            if( i == 0 ){
                                i = 1;
                            }else{
                                i-- ;
                            }
                        }

                        send( mkEnvmsgMsg( result[i].getName().getLocalName() , "0" ) );
                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
            
        });
        

        
        
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                
                ACLMessage msg = myAgent.receive() ;
                
                if( msg != null ){
                    
                    String[] parts = msg.getContent().split("\\s+" , 2) ;
                    String cmdName = parts[0];
                    String rest = parts[1];
                    
                    if( "envmsg".equals(cmdName) ){
                        String[] parts_ = rest.split("\\s+" , 2) ;
                        String senderLocalName = parts_[0];
                        String envmsg = parts_[1];
                        
                        Integer newNum = Integer.parseInt(envmsg) + 1;
                        
                        send( mkEnvmsgMsg( senderLocalName , newNum.toString() ) );                       
                        
                    }        
                }
            }
        }); 
        
        
        
        
    }

    
    
    
}
