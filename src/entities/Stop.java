package entities;

import java.util.List;
import java.util.Queue;

public class Stop {

    private String name;
    private boolean isBusy;
    private int direction; // 0 pnr-> station | 1 Station->pnr
    Queue<Passenger> passengerList;



    int maxWaitingTime;
    int totalWaitingTime;
    int numWaitPassenger;
}
