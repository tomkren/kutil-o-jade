package trhy;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tomáš Křen
 */
public class Firm {
    
    private String firmID;
    private Map<Comodity,Elem> inventory;
    private double money; // protože se do nich šaha opravdu často,
                          // nedáme je pro efektivitu do mapy 
    
    public Firm (String firmID) {
        this.firmID = firmID;
        inventory = new HashMap<Comodity, Elem>();
        money = 0;
    }
    
    public boolean hasEnoughMoney (double m) {
        return money >= m; 
    }
    
    public boolean hasEnoughComodity (Comodity c, double num) {
        Elem e = inventory.get(c);
        if (e == null) {return false;}
        return e.getNum() >= num;
    }
    
    public String getFirmID () {
        return firmID;
    }
    
    public Map<Comodity,Elem> getInventoryMap () {
        return inventory;
    }
    
    public static interface Elem {
        public double getNum();
    }
    
    public static class NumElem implements Elem {
        double num;
        public double getNum() {
            return num;
        }        
    }

}
