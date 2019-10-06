package events;

import entities.Tram;

import java.time.LocalTime;

public class Event {

    int eventType;
    LocalTime eventTime;
    private Tram tram;

    /* 1 : arrival at intermediate  stop , 2 : departure from intermediate stop, 3- Arriving at endpoint, 4-departure at end stop
    * 5- arrival at switch , 6- departure from switch , 7- Changing tracks (cross), 8- going straight at switch,
    * 9- passenger arrival
    * */



    public Event(int eventType, LocalTime eventTime, Tram tram) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.tram = tram;
    }

    void eventHandler(){
        switch (eventType) {
            case 1:
                eventHandlerArrivingStop();
            case 2:
                eventHandlerDepartureStop();
        }
    }

    void eventHandlerArrivingStop(){

    }

    void eventHandlerDepartureStop(){

    }


    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }

    public Tram getTram() {
        return tram;
    }

    public void setTram(Tram tram) {
        this.tram = tram;
    }
}
