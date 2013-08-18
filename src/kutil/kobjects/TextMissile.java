package kutil.kobjects;

import kutil.core.*;
import kutil.items.StringItem;
import kutil.shapes.MrShape;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.World;



/**
 *
 * @author Tomáš Křen
 */
public class TextMissile extends Symbol {
    
    private StringItem target;
    private StringItem from;
    
    private KObject targetKObject ;
    
    private World parentWorld;
    
    private TMCollisionListener listener;
    
    public TextMissile( KAtts kAtts ) {
        super(kAtts);
        target = items().addString(kAtts , "target" , null );
        from   = items().addString(kAtts , "from"   , null );
        
        create();
    }

    /**
     * kopírovací konstruktor.
     */
    public TextMissile( TextMissile tm ) {
        super(tm);
        target = items().addString( "target" , tm.target.get() );
        from   = items().addString( "target" , tm.from.get()   );
        create();
    }

    @Override
    public KObject copy() {
        return new TextMissile(this);
    }

    private void create(){
        setType( "textMissile" );
        setPhysical(true);
        setAttached(false);
        
        targetKObject = Global.idDB().get( target.get() );
    }
    
    
    private Int2D goalPos(){
        return targetKObject.pos() ;
    }
    
    private static final float speed = 60;
    
    private void stepToGoal(){

        Vector2f v = Int2D.toROVector2f( goalPos() );
        Body body = getBody();

        v.sub( body.getPosition() );

        v.normalise();
        v.scale( speed );

        Utils.stopBody( body );
        body.adjustVelocity( v );

    }
    
    public void step() {
        super.step();
        
        if( ! Global.rucksack().isSimulationRunning() ) return;

        stepToGoal();
    }
    
    
    @Override
    public void init() {
        super.init();
        resetCollisionListener();
    }

    @Override
    public void setParent(KObject newParent) {
        super.setParent(newParent);
        resetCollisionListener();
    }
    
   

    
    private void resetCollisionListener(){
        if( parentWorld != null ){
            parentWorld.removeListener(listener);
        }

        parentWorld = getParentWorld();

        if( parentWorld != null ){
            listener = new TMCollisionListener(this);
            parentWorld.addListener( listener );
        }

    }
    
    @Override
    public void delete() {
        super.delete();
        if( parentWorld != null ){
            parentWorld.removeListener(listener);
        }
    }
    
    public void handleHit( KObject hitKObject ){
        
        if( hitKObject == targetKObject ){
            
            Global.rucksack().getScheduler().informAboutEnvMsgHitTarget( from.get() , target.get() , val.get() );
            
            delete();
            
        }
        
    }
    
    

}
class TMCollisionListener implements CollisionListener {

    private TextMissile tm;

    public TMCollisionListener( TextMissile tm ){
        this.tm = tm;
    }


    public void collisionOccured(CollisionEvent event) {

        Body hitBody = null;
        if ( event.getBodyA() == tm.getBody() ) {
            hitBody = event.getBodyB();
        } else if(  event.getBodyB() == tm.getBody() ) {
            hitBody = event.getBodyA();
        }
        
        if( hitBody == null ) return;
        
        tm.handleHit( (KObject) hitBody.getUserData() );
    }
}