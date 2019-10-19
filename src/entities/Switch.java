package entities;

import java.util.LinkedList;
import java.util.Queue;

public class Switch {

    private EndStop station;
    private Queue<Tram> incomming, outgoing;
    private boolean straight_in_busy, skewed_in_busy, straight_out_busy, skewed_out_busy;

    public Switch(EndStop station){
        this.station  = station;
        incomming = new LinkedList<Tram>(); outgoing = new LinkedList<Tram>();
        straight_in_busy = skewed_in_busy = straight_out_busy = skewed_out_busy = false;
    }

    public void addIncomming(Tram tram){
        incomming.add(tram);
    }

    public void addOutgoing(Tram tram){
        outgoing.add(tram);
    }

    public Tram pollIncomming(){
        return incomming.poll();
    }

    public Tram pollOutgoing(){
        return outgoing.poll();
    }

    public Tram peakIncomming(){
        return incomming.peek();
    }

    public Tram peakOutgoing(){
        return outgoing.peek();
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
