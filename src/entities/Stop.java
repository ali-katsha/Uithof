package entities;

import java.util.*;

public class Stop {
    public Stop(String name, int direction,int stopNumber) {

        this.name = name;
        this.direction = direction;
        this.stopNumber = stopNumber;

        isBusy = false;
        passengerQueue = new LinkedList<>();
        maxWaitingTime=0;
        totalWaitingTime=0;
        numWaitPassenger=0;
        waitingTrams = new LinkedList<>();
    }


    public Stop(String name,int stopNumber) {

        this.name = name;
        this.direction = 0;
        this.stopNumber = stopNumber;

        isBusy = false;
        passengerQueue = new LinkedList<>();
        waitingTrams = new LinkedList<>();

        maxWaitingTime=0;
        totalWaitingTime=0;
        numWaitPassenger=0;
    }

    private String name;
    private boolean isBusy;
    private int direction; // 0 Station->pnr | 1 pnr-> station |
    Queue<Passenger> passengerQueue;
    private int stopNumber;
    Queue<Tram>  waitingTrams ;

    public void addTramtoWaitingTrams(Tram tram){
        waitingTrams.add(tram);
    }

    long maxWaitingTime;
    long totalWaitingTime;
    long numWaitPassenger;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Queue<Passenger> getPassengerQueue() {
        return passengerQueue;
    }

    public void setPassengerQueue(Queue<Passenger> passengerQueue) {
        this.passengerQueue = passengerQueue;
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

    public void setTotalWaitingTime(long totalWaitingTime) {
        this.totalWaitingTime = totalWaitingTime;
    }

    public long getNumWaitPassenger() {
        return numWaitPassenger;
    }

    public void setNumWaitPassenger(long numWaitPassenger) {
        this.numWaitPassenger = numWaitPassenger;
    }


    public int getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(int stopNumber) {
        this.stopNumber = stopNumber;
    }


    public Queue<Tram> getWaitingTrams() {
        return waitingTrams;
    }

    public void setWaitingTrams(Queue<Tram> waitingTrams) {
        this.waitingTrams = waitingTrams;
    }

}
