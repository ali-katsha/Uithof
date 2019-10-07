package entities;

import java.time.LocalTime;

public class Passenger {

    public Passenger(LocalTime arrivingTime, Stop stop) {
        this.arrivingTime = arrivingTime;
        this.stop = stop;
    }

    LocalTime arrivingTime;
    Stop stop;
    int waitingTime;

    public LocalTime getArrivingTime() {
        return arrivingTime;
    }

    public void setArrivingTime(LocalTime arrivingTime) {
        this.arrivingTime = arrivingTime;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
}
