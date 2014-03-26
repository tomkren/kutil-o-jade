package trhy;

import java.util.*;

/**
 *
 * @author Tomáš Křen
 */

public class Trh {
    
    private int currentTik;  // současnej čas simulace
    private int numTrans;    // aneb nasledující transaction ID 
    
    private Map<Comodity,Tabule>    tabs;      // komodita -> tabule tý komodity
    private Map<String,Firm>        firms;     // firmID   -> majetek tý firmy   
    private Map<String,Set<String>> ownership; // agentID  -> mn. firmID co má
                                               // pozdějc bude fikanějc, aby
                                               // šli i jiný vztahy než má
        
    public Trh () {
        tabs      = new HashMap<Comodity, Tabule>();
        firms     = new HashMap<String, Firm>();
        ownership = new HashMap<String, Set<String>>();
        
        numTrans   = 0;
        currentTik = 0;
    }
    
    public void addTabule (Comodity c) {
        tabs.put(c, new Tabule(c));
    }
    
    public void addAgentsFirm (String agentID, Firm hisFirm) throws TrhException {
        
        // TODO : projdi všechny komodity a pokud pro ně ještě neni trh tak ho udělat!
        
        String firmID = hisFirm.getFirmID();
        
        if (firms.containsKey(firmID)) {
            throw new TrhException("Firma s takovým názvem (firmID) už na trhu je.");
        }
        
        Set<String> hisFirmIDs = ownership.get(agentID);
        
        if (hisFirmIDs == null) {
            hisFirmIDs = new HashSet<String>();
            ownership.put(firmID, hisFirmIDs);
        }
        
        hisFirmIDs.add(firmID);
        firms.put(firmID, hisFirm);
    }
    
    public Transaction.Result checkTransactionRequest (Transaction.Request tr) {
        
        Transaction.TRHead head = tr.getHead();        
        
        if (!isOwner(head.agentID, head.firmID)) {
            return Transaction.ko("Agent není majitelem firmy.");
        }
        
        // todo : zvážit zda má opravdu smysl tohle zakazovat...
        if (tr instanceof Transaction.Slow) {
            Transaction.Slow slow = (Transaction.Slow) tr;
            if (slow.getPrice() <= 0) {return Transaction.ko("Price must be > 0.");}
        }
        
        Comodity comodity = head.comodity;
        
        // todo : zvážit zda nenahradit vytvořením té tabule radši
        if (!tabs.containsKey(comodity)){
            return Transaction.ko("Tato komodita se na trhu neobchoduje.");
        }

        Firm firm = firms.get(head.firmID);
        
        if (tr instanceof Transaction.Buy) {
            Transaction.Buy buy = (Transaction.Buy) tr;
            
            double money = buy.getMoney();
            if (money <= 0) {return Transaction.ko("Money must be > 0.");}
            
            if (firm.hasEnoughMoney(money)) {
                return Transaction.OK;
            } else {
                return Transaction.ko("Firma nemá požadované množství peněz.");
            }
        }
        
        if (tr instanceof Transaction.Sell) {
            Transaction.Sell sell = (Transaction.Sell) tr;

            double num = sell.getNum();
            if (num <= 0) {return Transaction.ko("Num must be > 0.");}
            
            if (firm.hasEnoughComodity(comodity,num)) {
                return Transaction.OK;
            } else {
                return Transaction.ko("Firma nemá požadované množství komodity.");
            }
        }
        
        return Transaction.ko("Unsupported transaction request format.");
    }
    
   
    
    public boolean isOwner (String agentID, String firmID) {
        Set<String> hisFirms = ownership.get(agentID);
        if (hisFirms == null) {return false;}
        return hisFirms.contains(firmID);
    }

    
    
    
    
    
    public static class TrhException extends Exception {

        private String msg;
        
        public TrhException(String msg) {
            this.msg = msg;
        }
        
        @Override
        public String getMessage() {
            return "[TRH EXCEPTION] : "+msg;
        }
        
    }
    
    
}
