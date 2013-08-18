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
public class Ping extends KutilGhostAgent {

    @Override
    protected void setup() {
        super.setup();
        
        addBehaviour( new OneShotBehaviour(this) {
            @Override
            public void action() {
                
               
               send( mkIncarnateMsg( new Int2D( 100, 300) ) ); 
               
               send( mkEnvmsgMsg( "pong" , "piiiing" ) );
               
               
                
            }
        });
        
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive() ;
                if( msg != null ){
                    Log.it("Ping dostal : " + msg.getContent()  );
                    send( mkEnvmsgMsg( "pong" , "piiiiing" ) );
                }
            }
        }); 
        
        
    }
    
    

}
