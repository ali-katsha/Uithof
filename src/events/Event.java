package events;

import java.time.LocalTime;

public class Event implements Comparable<Event>{
    public int eventType;
    public LocalTime eventTime;

    public int compareTo(Event event) {
        if (this.equals(event)) {
            return 0;
        } else if (this.eventTime.isAfter(event.eventTime)) {
            return 1;
        } else {
            return -1;
        }
    }
}
