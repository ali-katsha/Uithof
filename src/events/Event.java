package events;

import entities.Passenger;
import entities.Stop;
import entities.Tram;
import generators.DrivingTimeGenerator;
import generators.PassengersArrivingGenerator;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Event implements Comparable<Event>{

    int eventType;
    LocalTime eventTime;
    private Tram tram;
    private Stop stop;

    /* 1 : arrival at intermediate  stop , 2 : departure from intermediate stop, 3- Arriving at endpoint, 4-departure at end stop
    * 5- arrival at switch , 6- departure from switch , 7- Changing tracks (cross), 8- going straight at switch,
    * 9- passenger arrival
    * */



    public Event(int eventType, LocalTime eventTime, Tram tram) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.tram = tram;
    }
    public Event(int eventType, LocalTime eventTime, Stop stop) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.stop = stop;
    }

    public PriorityQueue<Event>  eventHandler(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS) throws IOException {
        switch (eventType) {
            case 1:
                return eventHandlerArrivingStop(eventQueue,routeCSPNR,routePNRCS);
            case 2:
                return eventHandlerDepartureStop( eventQueue,routeCSPNR,routePNRCS);
            case 9:
                return eventHandlerPassengersArrival( eventQueue);
        }
        return eventQueue;
    }

    PriorityQueue<Event> eventHandlerPassengersArrival(PriorityQueue<Event> eventQueue) throws IOException {

        int numPass = PassengersArrivingGenerator.getNumPassengers(stop,eventTime,"in");
        for (int i=0;i<numPass;i++){
            stop.getPassengerQueue().add(new Passenger(eventTime,stop));
        }

        Event nextArrivingEvent = new Event(9,eventTime.plusMinutes(15),stop);
        eventQueue.add(nextArrivingEvent);
        return eventQueue;

    }

    PriorityQueue<Event> eventHandlerArrivingStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS) throws IOException {



        if (tram.getNextStop().getWaitingTrams().peek() == tram){
            //stop busy
            tram.getNextStop().setBusy(true);

            // Passengers in out
          //  int passengersOut =PassengersArrivingGenerator.getNumPassengers(tram.getNextStop(),eventTime,"out");
            int passengersOut =0;
            int currentPassenger = tram.getPassengersNumber()-passengersOut;

            int numPassAllowed = tram.getMaxCapacity() - currentPassenger;
            int passengersCounter = 0;

            Queue<Passenger> passengerQueue= tram.getNextStop().getPassengerQueue();

            while (!passengerQueue.isEmpty()){
                passengerQueue.remove();
                passengersCounter++;
                if (passengersCounter>numPassAllowed)
                    break;
            }

            tram.setPassengersNumber(currentPassenger+passengersCounter);
            //End Passengers in out


            // Calculate travel time
            double dwellTime = 12.5 + 0.22* passengersCounter + 0.13 * passengersOut;
            long travelTime = ChronoUnit.SECONDS.between(tram.getPlannedArrivalTime(),tram.getDepartureTime());
            tram.setTravelTime(tram.getTravelTime()+travelTime+(long)dwellTime);
            // End Calculate travel time



            // Adjust stops
            Stop newNextStop = getNextStop(tram.getNextStop(),routeCSPNR,routePNRCS);
            tram.setCurrentStop(tram.getNextStop());
            tram.setNextStop(newNextStop);



            //setup departure time
            LocalTime departureTime = tram.getPlannedArrivalTime().plusSeconds((long)dwellTime);
            Event departure = new Event(2,departureTime,tram);

            eventQueue.add(departure);
            return eventQueue;

        }
        else
            return eventQueue;

/*
        if (tram.getNextStop().isBusy()){
            tram.getNextStop().getWaitingTrams().add(tram);
            return eventQueue;
        }

        if(tram.getNextStop().getWaitingTrams().size() >2){

            LocalTime newArrival =tram.getNextStop().getWaitingTrams().peek().getPlannedArrivalTime().plusSeconds(40);
            // we still need dwell time from the
            tram.setPlannedArrivalTime( newArrival);
        }
        return eventQueue;

        */

    }

    PriorityQueue<Event>  eventHandlerDepartureStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){


        // set free and schedule new arriving

        if (!tram.getCurrentStop().getWaitingTrams().isEmpty()){
            tram.getCurrentStop().setBusy(false);
            tram.getCurrentStop().getWaitingTrams().remove();
            if (!tram.getCurrentStop().getWaitingTrams().isEmpty()) {
                Event arrivingEvent = new Event(1, eventTime.plusSeconds(40), tram.getCurrentStop().getWaitingTrams().peek());
                eventQueue.add(arrivingEvent);
            }
        }

        /*if (!tram.getCurrentStop().getWaitingTrams().isEmpty()) {
            tram.getCurrentStop().getWaitingTrams().peek().setPlannedArrivalTime(eventTime.plusSeconds(40));
            Event arriving = new Event(1, eventTime.plusSeconds(40), tram.getCurrentStop().getWaitingTrams().peek());
        }*/

        long drivingTime = DrivingTimeGenerator.generateDrivingTime(tram.getCurrentStop(),tram.getNextStop());

        Event arrivingEvent = new Event(1,eventTime.plusSeconds(drivingTime),this.tram);


        tram.setPlannedArrivalTime(eventTime.plusSeconds(drivingTime));
        tram.setDepartureTime(eventTime);

        //add tram to queue
        tram.getNextStop().addTramtoWaitingTrams(tram);


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
        }else if  (stop.getStopNumber() ==9) {
            return routePNRCS.get(1);
        }
        else if  (stop.getStopNumber() <17){
                return routePNRCS.get(stop.getStopNumber()-10);
        }
        else if  (stop.getStopNumber() == 17){
            return routeCSPNR.get(0);
        }
        return null;
    }



    public int compareTo(Event event) {
        if (this.equals(event)) {
            return 0;
        } else if (this.eventTime.isAfter(event.getEventTime())) {
            return 1;
        } else {
            return -1;
        }
    }

}
