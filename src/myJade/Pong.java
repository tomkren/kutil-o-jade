package myJade;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import kutil.core.Int2D;
import kutil.core.Log;

/**
 *
 * @author Tomáš Křen
 */
public class Pong extends KutilGhostAgent {
    
    @Override
    protected void setup() {
        super.setup();
        
        addBehaviour( new OneShotBehaviour(this) {
            @Override
            public void action() {
                
               
               send( mkIncarnateMsg( new Int2D( 500, 300) ) ); 
                

               
               
                
            }
        });
        
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive() ;
                if( msg != null ){
                    Log.it("Pong dostal : " + msg.getContent()  );
                    
                    
                    
                    String[] parts = msg.getContent().split("\\s+" , 2) ;
                    String cmdName = parts[0];
                    String rest = parts[1];
                    
                    if( "envmsg".equals(cmdName) ){
                        String[] parts_ = rest.split("\\s+" , 2) ;
                        String senderLocalName = parts_[0];
                        String envmsg = parts_[1];
                        
                        send( mkEnvmsgMsg( senderLocalName , envmsg + "!" ) );                       
                        
                        
                    }
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                }
            }
        }); 
        
    
        
        
    }

}
