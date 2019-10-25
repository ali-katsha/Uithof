package entities;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EndStop extends Stop{
    private Switch aSwitch;

    private Tram tram_A;
    private Tram tram_B;

    List<LocalTime> plannedArrival;
    List<LocalTime> plannedDeparture;

    List<Long> departureDelayList;

    public EndStop(String name,int stopNumber) {
        super(name,stopNumber);
        aSwitch = new Switch();
        departureDelayList = new ArrayList<>();
    }

    public Switch getaSwitch() {
        return aSwitch;
    }

    public Tram getTram_A() {
        return tram_A;
    }

    public void setTram_A(Tram tram) { tram_A = tram; }

    public Tram getTram_B() {
        return tram_B;
    }

    public void setTram_B(Tram tram) { tram_B = tram; }

    public List<Long> getDepartureDelayList() {
        return departureDelayList;
    }

    public void addDepartureDelay(Long delay){ departureDelayList.add(delay); }

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
