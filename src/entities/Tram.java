package entities;

import java.time.LocalTime;
import java.util.List;

public class Tram {


    public Tram() {

    }

    public Tram(int tramNum, Stop currentStop, Stop nextStop, LocalTime departureTime) {
        this.tramNum = tramNum;
        this.currentStop = currentStop;
        this.nextStop = nextStop;
        this.departureTime = departureTime;
        this.travelTime =0;
        this.passengersNumber = 0;
    }

    private static final int MAX_CAPACITY = 420;
    private int tramNum;
    private String direction;

    private Stop currentStop;
    private Stop nextStop;

    private LocalTime departureTime;
    private LocalTime plannedArrivalTime;

    private long travelTime;
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

    public long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(long travelTime) {
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

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getPlannedArrivalTime() {
        return plannedArrivalTime;
    }

    public void setPlannedArrivalTime(LocalTime plannedArrivalTime) {
        this.plannedArrivalTime = plannedArrivalTime;
    }
}
