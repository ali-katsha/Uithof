package entities;

import java.util.List;

public class Tram {

    private static final int MAX_CAPACITY = 420;
    private int tramNum;
    private String direction;

    private Stop currentStop;
    private Stop nextStop;

    private int travelTime;
    private int passengersNumber;

    private boolean waiting; //

    public static int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public int getTramNum() {
        return tramNum;
    }

    public void setTramNum(int tramNum) {
        this.tramNum = tramNum;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Stop getCurrentStop() {
        return currentStop;
    }

    public void setCurrentStop(Stop currentStop) {
        this.currentStop = currentStop;
    }

    public Stop getNextStop() {
        return nextStop;
    }

    public void setNextStop(Stop nextStop) {
        this.nextStop = nextStop;
    }

    public int getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(int travelTime) {
        this.travelTime = travelTime;
    }

    public int getPassengersNumber() {
        return passengersNumber;
    }

    public void setPassengersNumber(int passengersNumber) {
        this.passengersNumber = passengersNumber;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }
}
