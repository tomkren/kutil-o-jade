package trhy;

import java.util.*;
import kutil.core.Log;

/**
 *
 * @author Tomáš Křen
 */


public class Tabule {
    
    private Comodity comodity;

    private PriorityQueue<Row> supply;
    private PriorityQueue<Row> demand;
            
    public static class Row {
        
        int    transID;   // transaction ID
        String firmID;    // firm ID
        double price;     // price
        double num;       // number of items
        int    tik;       // tik zadání
        
        public Row (int tid, String fid, double p, double n, int t) {
            transID = tid;
            firmID  = fid;
            price   = p;
            num     = n;
            tik     = t;
        } 
        
        @Override
        public String toString() {
            return "$"+price+" ... "+num+" ks ... "+firmID;
        }
    }

    
    public Tabule(Comodity comodity) {
        this.comodity = comodity;
        int initialCapacity = 11; //11 je prej default
        supply = new PriorityQueue<Row>(initialCapacity, new MinRowComparator()); 
        demand = new PriorityQueue<Row>(initialCapacity, new MaxRowComparator());
    }
    
    public Row bestDemand () {
        return demand.peek();
    }
    
    public Row bestSupply () {
        return supply.peek();
    }
    
    public double buyMarketPrice () {
        return bestSupply().price;
    }
    
    public double sellMarketPrice () {
        return bestSupply().price;
    }
    
    public static void main (String[] args) {
        Log.it("Tabule main, hello!");
        
        Tabule t = new Tabule(new BasicComodity("Koláč"));
               
        t.supply.add(new Row(3, "Koloniál", 44, 3    ,10));
        t.supply.add(new Row(1, "Pekař&Syn", 42, 10  ,1));
        t.supply.add(new Row(2, "Pekař&Syn", 43, 110 ,5));

        t.demand.add(new Row(30, "UKsro", 41, 3,100));
        t.demand.add(new Row(10, "Koloniál", 40, 10,20));
        t.demand.add(new Row(20, "UKsro", 41.5, 110,3));
        
        
        Log.it(t.toString());
        
        Log.it("Best supply price: $" + t.supply.peek().price);
        Log.it("Best demand price: $" + t.demand.peek().price);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-- ").append(comodity).append(" ----------\n");
        
        Row[] sArr = supply.toArray(new Row[0]);
        Row[] dArr = demand.toArray(new Row[0]);
        
        Arrays.sort(sArr, new MaxRowComparator());
        Arrays.sort(dArr, new MaxRowComparator());

        for (Row r : sArr) { sb.append(r.toString()).append("\n"); } 
        sb.append("\n");
        for (Row r : dArr) { sb.append(r.toString()).append("\n"); }
        
        sb.append("-------------------");
        
        return sb.toString();
    }
    
    
    
    class MinRowComparator implements Comparator<Row> {
        public int compare(Row r1, Row r2) {
            if (r1.price   < r2.price)   {return -1;}
            if (r1.price   > r2.price)   {return  1;}
            if (r1.transID < r2.transID) {return -1;}    
            return 1;
        }        
    }

    class MaxRowComparator implements Comparator<Row> {
        public int compare(Row r1, Row r2) {
            if (r1.price   < r2.price)   {return  1;}
            if (r1.price   > r2.price)   {return -1;}
            if (r1.transID < r2.transID) {return -1;}    
            return 1;
        }        
    }
    

    
}
