package entities;

import java.time.LocalTime;
import java.util.List;

public class EndStop extends Stop{



    private Switch aSwitch;

    boolean isABusy;
    boolean isBBusy;

    List<Passenger> passengerList;

    List<LocalTime> plannedArrival;
    List<LocalTime> plannedDeparture;



    public EndStop(String name) {
        super(name);
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
