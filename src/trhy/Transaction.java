package trhy;

/**
 *
 * @author Tomáš Křen
 */
public class Transaction {
    
    public static class Request {
        private TRHead head;

        TRHead getHead() {
            return head;
        }
    }
    
    public static interface Buy {
        double getMoney();
    }

    public static interface Sell {
        double getNum();
    }
    
    public static interface Quick {}
    
    public static interface Slow {
        double getPrice();
    }
    
    public static class TRHead {
        String   agentID;
        String   firmID;
        Comodity comodity;
    }

    
    
    public static class QBuy extends Request implements Quick, Buy {
        double money;

        public double getMoney() {
            return money;
        }  
    }
    
    public static class QSell extends Request implements Quick, Sell {
        double num;
        
        public double getNum() {
            return num;
        }
    }
    
    public static class SBuy extends Request implements Slow, Buy {
        double money;
        double price;
        
        public double getMoney() {
            return money;
        }  
        public double getPrice() {
            return price;
        }
    }
    
    public static class SSell extends Request implements Slow, Sell {
        double num;
        double price;
        
        public double getNum() {
            return num;
        }
        public double getPrice() {
            return price;
        }
    }
    
    
    
    public static class Result {
        boolean ok;
        String msg;
        
        public Result(boolean isOk, String m){
            ok  = isOk;
            msg = m;
        } 
    }
    
    public static final Result OK = new Result(true, null);
    public static Result ko(String msg){
        return new Result(false, msg);
    }
    
}
