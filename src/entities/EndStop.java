package entities;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EndStop extends Stop{



    private Switch aSwitch;

    boolean isABusy;
    boolean isBBusy;

    List<Passenger> passengerList;

    List<LocalTime> plannedArrival;
    List<LocalTime> plannedDeparture;

    List<Long> departureDelayList;

    public EndStop(String name,int stopNumber) {
        super(name,stopNumber);
        departureDelayList = new ArrayList<>();
    }

    public Switch getaSwitch() {
        return aSwitch;
    }

    public void setaSwitch(Switch aSwitch) {
        this.aSwitch = aSwitch;
    }

    public boolean isABusy() {
        return isABusy;
    }

    public List<Long> getDepartureDelayList() {
        return departureDelayList;
    }

    public void addDepartureDelay(Long delay){
        System.out.println("Delay"+delay);
        departureDelayList.add(delay);

    }
    public void setABusy(boolean ABusy) {
        isABusy = ABusy;
    }

    public boolean isBBusy() {
        return isBBusy;
    }

    public void setBBusy(boolean BBusy) {
        isBBusy = BBusy;
    }

    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<Passenger> passengerList) {
        this.passengerList = passengerList;
    }

    public List<LocalTime> getPlannedArrival() {
        return plannedArrival;
    }

    public void setPlannedArrival(List<LocalTime> plannedArrival) {
        this.plannedArrival = plannedArrival;
    }

    public List<LocalTime> getPlannedDeparture() {
        return plannedDeparture;
    }

    public void setPlannedDeparture(List<LocalTime> plannedDeparture) {
        this.plannedDeparture = plannedDeparture;
    }
}
