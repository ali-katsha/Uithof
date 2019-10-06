package events;

import entities.Stop;
import entities.Tram;
import generators.DrivingTimeGenerator;

import java.time.LocalTime;
import java.util.List;
import java.util.PriorityQueue;

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

    PriorityQueue<Event>  eventHandler(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){
        switch (eventType) {
            case 1:
                eventHandlerArrivingStop(eventQueue,routeCSPNR,routePNRCS);
            case 2:
                eventHandlerDepartureStop( eventQueue,routeCSPNR,routePNRCS);
        }
    }

    PriorityQueue<Event>  eventHandlerArrivingStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){

    }

    PriorityQueue<Event>  eventHandlerDepartureStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){

        long drivingTime = DrivingTimeGenerator.generateDrivingTime(tram.getCurrentStop(),tram.getNextStop());

        Event arrivingEvent = new Event(1,eventTime.plusSeconds(drivingTime),this.tram);


        eventQueue.add(arrivingEvent);
        return eventQueue;
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

    private Stop getNextStop(Stop stop ,List<Stop> routeCSPNR ,List<Stop> routePNRCS){
        if (stop.getStopNumber() <9){
            return routeCSPNR.get(stop.getStopNumber());
        }else if  (stop.getStopNumber() <16){
            return routePNRCS.get(stop.getStopNumber()-8);
        }

    }

}
