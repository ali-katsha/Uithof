package entities;

import java.util.PriorityQueue;

public class Switch {

    private EndStop station;
    private PriorityQueue<Tram> incomming, outgoing;
    private boolean straight_in_busy, skewed_in_busy, straight_out_busy, skewed_out_busy;

    public Switch(EndStop station){
        this.station  = station;
        incomming = outgoing = new PriorityQueue<Tram>();
        straight_in_busy = skewed_in_busy = straight_out_busy = skewed_out_busy = false;
    }

    public PriorityQueue<Tram> getIncomming(){
        return incomming;
    }

    public PriorityQueue<Tram> getOutgoing(){
        return outgoing;
    }

    public boolean Straight_in_busy(){
        return straight_in_busy;
    }

    public boolean Skewed_in_busy(){
        return skewed_in_busy;
    }

    public boolean Straight_out_busy(){
        return straight_out_busy;
    }

    public boolean Skewed_out_busy(){
        return skewed_out_busy;
    }

    public void Set_straight_in_busy(boolean val){
        straight_in_busy = val;
    }

    public void Set_skewed_in_busy(boolean val){
        skewed_in_busy = val;
    }

    public void Set_straight_out_busy(boolean val){
        straight_out_busy = val;
    }

    public void Set_skewed_out_busy(boolean val){
        skewed_out_busy = val;
    }
}
