package entities;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Stop {
    private String name;
    private boolean isBusy;
    Queue<Passenger> passengerQueue;
    private int stopNumber;
    Queue<Tram>  waitingTrams ;

    long maxWaitingTime;
    long totalWaitingTime;
    long numWaitPassenger;

    public Stop(String name,int stopNumber) {
        this.name = name;
        this.stopNumber = stopNumber;

        isBusy = false;
        passengerQueue = new LinkedList<>();
        waitingTrams = new LinkedList<>();

        maxWaitingTime=0;
        totalWaitingTime=0;
        numWaitPassenger=0;
    }

    public Queue<Passenger> getPassengerQueue() {
        return passengerQueue;
    }

    public void updateWaitingTime(Passenger passenger, LocalTime time){
        long waiting = ChronoUnit.SECONDS.between(passenger.getArrivingTime(),time);
        if (waiting >maxWaitingTime)
            setMaxWaitingTime(waiting);
        totalWaitingTime+=waiting;
        numWaitPassenger++;
    }
    public long getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public void setMaxWaitingTime(long maxWaitingTime) {
        this.maxWaitingTime = maxWaitingTime;
    }

    public long getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public long getNumWaitPassenger() {
        return numWaitPassenger;
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public Queue<Tram> getWaitingTrams() {
        return waitingTrams;
    }

    public void addTramtoWaitingTrams(Tram tram){ waitingTrams.add(tram); }

    public String getName() {
        return name;
    }

    public void setBusy(boolean busy) { isBusy = busy; }
}
