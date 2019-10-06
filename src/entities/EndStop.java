package entities;

import java.time.LocalTime;
import java.util.List;

public class EndStop {

    public EndStop(String name){
        this.name = name;

    }

    private Switch aSwitch;
    private String name;

    boolean isABusy;
    boolean isBBusy;

    List<Passenger> passengerList;

    List<LocalTime> plannedArrival;
    List<LocalTime> plannedDeparture;



    int maxWaitingTime;
    int totalWaitingTime;
    int numWaitPassenger;
}
