package events;

import entities.EndStop;
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
      //  if (eventType != 9)System.out.println(eventType);
        switch (eventType) {
            case 1:
                return eventHandlerArrivingStop(eventQueue,routeCSPNR,routePNRCS);
            case 2:
                return eventHandlerDepartureStop( eventQueue,routeCSPNR,routePNRCS);
            case 3:
                return eventHandlerArrivingEndStop(eventQueue,routeCSPNR,routePNRCS);
            case 4:
                return eventHandlerDepartureEndStop( eventQueue,routeCSPNR,routePNRCS);
            case 5:
                return eventHandlerArrivingSwitch(eventQueue,routeCSPNR,routePNRCS);
            case 6:
                return eventHandlerDepartureSwitch( eventQueue,routeCSPNR,routePNRCS);
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


        System.out.println("Arriving, Tram:"+tram.getTramNum()+" Time:"+eventTime);

        if (tram.getNextStop().getWaitingTrams().peek() == tram){
            if (!tram.getPlannedArrivalTime().equals(eventTime)){
                if (tram.getPlannedArrivalTime().isAfter(eventTime)){
                 //   System.out.println(1);
                    Event event = new Event(1,tram.getPlannedArrivalTime(),tram);
                    eventQueue.add(event);
                    return eventQueue;
                }
              //  System.out.println(2);
            }

            //stop busy
            tram.getNextStop().setBusy(true);

            // Passengers in out
            int passengersOut =PassengersArrivingGenerator.getNumPassengers(tram.getNextStop(),eventTime,"out");
          //  int passengersOut =0;

            int currentPassenger = tram.getPassengersNumber()-passengersOut;
            if(currentPassenger<0) { currentPassenger =0; System.out.print(" Pass Out =" + tram.getPassengersNumber());}
            else  System.out.print(" Pass Out =" + passengersOut);


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
            System.out.println(" Pass In = "+passengersCounter + " Pass in Tram =" + tram.getPassengersNumber());
            //End Passengers in out


            // Calculate travel time
            double dwellTime = 12.5 + 0.22* passengersCounter + 0.13 * passengersOut;
            long travelTime = ChronoUnit.SECONDS.between(tram.getPlannedArrivalTime(),tram.getDepartureTime());
            tram.setTravelTime(tram.getTravelTime()+travelTime+(long)dwellTime);
            // End Calculate travel time



            // Adjust stops
                Stop newNextStop = getNextStop(tram.getNextStop(), routeCSPNR, routePNRCS);

                tram.setCurrentStop(tram.getNextStop());
                tram.setNextStop(newNextStop);



            //setup departure time
            LocalTime departureTime = tram.getPlannedArrivalTime().plusSeconds((long)dwellTime);
            Event departure = new Event(2,departureTime,tram);

            eventQueue.add(departure);
            return eventQueue;

        }
        else {
            System.out.println("wait");
            return eventQueue;
        }

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

        //System.out.println();
        // set free and schedule new arriving

        if (!tram.getCurrentStop().getWaitingTrams().isEmpty()){
            System.out.print("Departure, Tram:"+tram.getTramNum()+" Time:"+eventTime+" - Departure from stop  : "+tram.getCurrentStop().getName()+" - "+tram.getCurrentStop().getStopNumber());

            tram.getCurrentStop().setBusy(false);
            tram.getCurrentStop().getWaitingTrams().remove();
            if (!tram.getCurrentStop().getWaitingTrams().isEmpty()) {
                System.out.println(4);
                Tram waitingTram = tram.getCurrentStop().getWaitingTrams().peek();
                if (waitingTram.getPlannedArrivalTime().isBefore(eventTime.plusSeconds(40))){
                    Event arrivingEvent = new Event(1, eventTime.plusSeconds(40), waitingTram);
                    eventQueue.add(arrivingEvent);
                    waitingTram.setPlannedArrivalTime(eventTime.plusSeconds(40));
                    System.out.println(5);
                }
            }
        }

        /*if (!tram.getCurrentStop().getWaitingTrams().isEmpty()) {
            tram.getCurrentStop().getWaitingTrams().peek().setPlannedArrivalTime(eventTime.plusSeconds(40));
            Event arriving = new Event(1, eventTime.plusSeconds(40), tram.getCurrentStop().getWaitingTrams().peek());
        }*/

        long drivingTime = DrivingTimeGenerator.generateDrivingTime(tram.getCurrentStop(),tram.getNextStop());
        System.out.println(" Driving time"+drivingTime);
        if (tram.getNextStop()== routeCSPNR.get(0) || tram.getNextStop()== routePNRCS.get(0) ){
            Event arrivingSwitchEvent = new Event(5, eventTime.plusSeconds(drivingTime), this.tram);
            eventQueue.add(arrivingSwitchEvent);
        }
        else {
            Event arrivingEvent = new Event(1, eventTime.plusSeconds(drivingTime), this.tram);
            eventQueue.add(arrivingEvent);
        }

        tram.setPlannedArrivalTime(eventTime.plusSeconds(drivingTime));
        tram.setDepartureTime(eventTime);

        //add tram to queue
        tram.getNextStop().addTramtoWaitingTrams(tram);



        return eventQueue;
    }

    PriorityQueue<Event> eventHandlerArrivingSwitch(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){
        Event event= new Event(3,eventTime,tram); // the arriving time to switch (event time) includes the driving time to the EndStop
        eventQueue.add(event); // You can get the queue from getWaitingTrams()
        //DON'T FORGET TO UPDATE PLANNED ARRIVAL TIME in  tram object
        return eventQueue;
    }

    PriorityQueue<Event> eventHandlerDepartureSwitch(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){
        Event event= new Event(1,eventTime,tram); // the event time is tricky here,  normally the event time includes the driving time So I am not sure if we should generate the driving time here rather than the stop Departure event
        eventQueue.add(event);
        //DON'T FORGET TO UPDATE PLANNED ARRIVAL TIME in tram object
        return eventQueue;
    }


    PriorityQueue<Event> eventHandlerArrivingEndStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS) throws IOException {


        System.out.println("Arriving, Tram:"+tram.getTramNum()+" Time:"+eventTime);

        if (tram.getNextStop().getWaitingTrams().peek() == tram){
            if (!tram.getPlannedArrivalTime().equals(eventTime)){
                if (tram.getPlannedArrivalTime().isAfter(eventTime)){

                    Event event = new Event(3,tram.getPlannedArrivalTime(),tram);
                    eventQueue.add(event);
                    return eventQueue;
                }

            }

            //stop busy
            tram.getNextStop().setBusy(true);

            // Passengers in out
            int passengersOut =tram.getPassengersNumber();
            //  int passengersOut =0;

            int currentPassenger = tram.getPassengersNumber()-passengersOut;
            if(currentPassenger<0) { currentPassenger =0; System.out.print(" Pass Out =" + tram.getPassengersNumber());}
            else  System.out.print(" Pass Out =" + passengersOut);


            int numPassAllowed = tram.getMaxCapacity() - currentPassenger;


            /// passenger getting in
            int passengersCounter = 0;
            Queue<Passenger> passengerQueue= tram.getNextStop().getPassengerQueue();
            while (!passengerQueue.isEmpty()){
                passengerQueue.remove();
                passengersCounter++;
                if (passengersCounter>numPassAllowed)
                    break;
            }
            tram.setPassengersNumber(currentPassenger+passengersCounter);

            System.out.println(" Pass In = "+passengersCounter + " Pass in Tram =" + tram.getPassengersNumber());
            //End Passengers in


            // Calculate travel time and dwell time for OUT ONLY
            double dwellTime = 12.5 + 0.13 * passengersOut;

            long travelTime = ChronoUnit.SECONDS.between(tram.getPlannedArrivalTime(),tram.getDepartureTime());
            tram.setTravelTime(tram.getTravelTime()+travelTime+(long)dwellTime);
            // End Calculate travel time



            // Adjust stops
            Stop newNextStop = getNextStop(tram.getNextStop(),routeCSPNR,routePNRCS);
            tram.setCurrentStop(tram.getNextStop());
            tram.setNextStop(newNextStop);


            // Get the time table and set the
            EndStop endStop = (EndStop) tram.getCurrentStop();
            LocalTime plannedDepartureEndStop = endStop.getPlannedDeparture().get(0);
            LocalTime plannedArrivalEndStop = endStop.getPlannedArrival().get(0);
            endStop.getPlannedArrival().remove(0);
            endStop.getPlannedDeparture().remove(0);

            tram.setPlannedArrivalEndStop(plannedArrivalEndStop);
            tram.setPlannedDepartureEndStop(plannedDepartureEndStop);

            tram.setDepartureTime(plannedDepartureEndStop);
            //setup departure time

            //LocalTime departureTime = tram.getPlannedArrivalTime().plusSeconds((long)dwellTime);
            Event departure = new Event(4,plannedDepartureEndStop,tram);

            eventQueue.add(departure);
            return eventQueue;

        }
        else {
            System.out.println("wait");
            return eventQueue;
        }





    }

    PriorityQueue<Event> eventHandlerDepartureEndStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){
        //System.out.println();
        // set free and schedule new arriving

        if (!tram.getCurrentStop().getWaitingTrams().isEmpty()){
            System.out.print("Departure, Tram:"+tram.getTramNum()+" Time:"+eventTime+" - Departure from stop : "+tram.getCurrentStop().getName()+" - "+tram.getCurrentStop().getStopNumber());

            tram.getCurrentStop().setBusy(false);
            tram.getCurrentStop().getWaitingTrams().remove();
            if (!tram.getCurrentStop().getWaitingTrams().isEmpty()) {
                System.out.println(4);
                Tram waitingTram = tram.getCurrentStop().getWaitingTrams().peek();
                if (waitingTram.getPlannedArrivalTime().isBefore(eventTime.plusSeconds(40))){
                    Event arrivingEvent = new Event(5, eventTime.plusSeconds(40), waitingTram);
                    eventQueue.add(arrivingEvent);
                    waitingTram.setPlannedArrivalTime(eventTime.plusSeconds(40));
                    System.out.println(5);
                }
            }
        }

        /*if (!tram.getCurrentStop().getWaitingTrams().isEmpty()) {
            tram.getCurrentStop().getWaitingTrams().peek().setPlannedArrivalTime(eventTime.plusSeconds(40));
            Event arriving = new Event(1, eventTime.plusSeconds(40), tram.getCurrentStop().getWaitingTrams().peek());
        }*/

        long drivingTime = DrivingTimeGenerator.generateDrivingTime(tram.getCurrentStop(),tram.getNextStop());
        System.out.println(" Driving time"+drivingTime);

        Event arrivingEvent = new Event(6, eventTime.plusSeconds(drivingTime), this.tram);
        eventQueue.add(arrivingEvent);

        tram.setPlannedArrivalTime(eventTime.plusSeconds(drivingTime));
        tram.setDepartureTime(eventTime);

        //add tram to queue
        tram.getNextStop().addTramtoWaitingTrams(tram);



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
        else if  (stop.getStopNumber() <17 && stop.getStopNumber() >10){
                return routePNRCS.get(stop.getStopNumber()-10+1);
        }
        else if  (stop.getStopNumber() == 17){
            return routeCSPNR.get(0);
        }
        return null;
    }


    private Stop getNextStop1(Stop stop ,List<Stop> routeCSPNR ,List<Stop> routePNRCS){
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
