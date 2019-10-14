package entities;

import java.time.LocalTime;
import java.util.List;

public class EndStop extends Stop{



    private Switch aSwitch;

    boolean isABusy;
    boolean isBBusy;

    private Tram tram_A;
    private Tram tram_B;

    List<Passenger> passengerList;

    List<LocalTime> plannedArrival;
    List<LocalTime> plannedDeparture;



    public EndStop(String name,int stopNumber) {
        super(name,stopNumber);
        aSwitch = new Switch(this);
    }

    public Switch getaSwitch() {
        return aSwitch;
    }

    public void setaSwitch(Switch aSwitch) {
        this.aSwitch = aSwitch;
    }

    public Tram getTram_A() {
        return tram_A;
    }

    public void setTram_A(Tram tram) { tram_A = tram; }

    public Tram getTram_B() {
        return tram_B;
    }

    public void setTram_B(Tram tram) { tram_B = tram; }

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
