package entities;

import java.time.LocalTime;

public class Passenger {
    LocalTime arrivingTime;
    Stop stop;

    public Passenger(LocalTime arrivingTime, Stop stop) {
        this.arrivingTime = arrivingTime;
        this.stop = stop;
    }

    public LocalTime getArrivingTime() {
        return arrivingTime;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }
}
