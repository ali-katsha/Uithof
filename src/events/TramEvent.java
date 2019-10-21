package events;

import entities.Tram;
import java.time.LocalTime;

public class TramEvent extends Event {
    public Tram tram;

    public TramEvent(int eventType, LocalTime eventTime, Tram tram) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.tram = tram;
    }
}
