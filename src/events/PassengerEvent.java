package events;

import entities.Stop;
import java.time.LocalTime;

public class PassengerEvent extends Event {
    public Stop stop;

    public PassengerEvent(int eventType, LocalTime eventTime, Stop stop) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.stop = stop;
    }
}