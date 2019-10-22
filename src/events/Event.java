package events;

import entities.*;
import generators.DrivingTimeGenerator;
import generators.DwellTimeGenerator;
import generators.PassengersArrivingGenerator;
import generators.PassengersOutGenerator;

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

    private static final LocalTime CALCULATION_START_TIME = LocalTime.of(7, 0, 0);
    private static final LocalTime CALCULATION_END_TIME = LocalTime.of(19, 0, 0);

    public static final int Frequency = 16;
  //  public static final long TURN_AROUND_TIME = 180;
//    public static final long TURN_AROUND_TIME = 240;
    public static final long TURN_AROUND_TIME = 300;

    private static final int SWITCH_STRAIGHT_TIME = 0;
    private static final int SWITCH_SKEWED_TIME = 60;


    private static final int NUM = 10;
    private static final boolean PRINT = false;
    private static final boolean PRINT_ALL = false;

    /* 1 : arrival at intermediate  stop , 2 : departure from intermediate stop, 3- Arriving at endpoint, 4-departure at end stop
    * 5- arrival at switch , 6- departure from switch , 7-passenger arrival
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
            case 3:
                return eventHandlerArrivingEndStop(eventQueue,routeCSPNR,routePNRCS);
            case 4:
                return eventHandlerDepartureEndStop( eventQueue,routeCSPNR,routePNRCS);
            case 5:
                return eventHandlerArrivingSwitch(eventQueue,routeCSPNR,routePNRCS);
            case 6:
                return eventHandlerDepartureSwitch( eventQueue,routeCSPNR,routePNRCS);
            case 7:
                return eventHandlerPassengersArrival( eventQueue);
        }
        return eventQueue;
    }

    PriorityQueue<Event> eventHandlerPassengersArrival(PriorityQueue<Event> eventQueue) throws IOException {

        int numPass = PassengersArrivingGenerator.getNumPassengers(stop,eventTime);
        for (int i=0;i<numPass;i++){
            stop.getPassengerQueue().add(new Passenger(eventTime,stop));
        }

        Event nextArrivingEvent = new Event(7,eventTime.plusMinutes(15),stop);
        eventQueue.add(nextArrivingEvent);
        return eventQueue;

    }

    PriorityQueue<Event> eventHandlerArrivingStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS) throws IOException {



        if (tram.getNextStop().getWaitingTrams().peek() == tram){
            if (!tram.getPlannedArrivalTime().equals(eventTime)){
                if (tram.getPlannedArrivalTime().isAfter(eventTime)){
                    Event event = new Event(1,tram.getPlannedArrivalTime(),tram);
                    eventQueue.add(event);
                    return eventQueue;
                }
            }

            //stop busy
            tram.getNextStop().setBusy(true);

            // Passengers in out
            int passengersOut = PassengersOutGenerator.getNumPassengers(tram,tram.getNextStop(),eventTime);
            int currentPassenger = tram.getPassengersNumber()-passengersOut;

            if(currentPassenger<0) { currentPassenger =0;
            //System.out.print(" Pass Out =" + tram.getPassengersNumber());
            }
            else{
                //System.out.print(" Pass Out =" + passengersOut);
            }


            int numPassAllowed = tram.getMaxCapacity() - currentPassenger;
            int passengersCounter = 0;


            boolean withinCalcTime = false;
            if (eventTime.isBefore(CALCULATION_END_TIME) && eventTime.isAfter(CALCULATION_START_TIME))
                 withinCalcTime = true;


            Queue<Passenger> passengerQueue= tram.getNextStop().getPassengerQueue();
            Stop currStop = tram.getNextStop();
            while (!passengerQueue.isEmpty()){
                if (withinCalcTime){
                    currStop.updateWaitingTime(passengerQueue.remove(),eventTime);
                }
                else
                    passengerQueue.remove();

                passengersCounter++;
                if (passengersCounter>numPassAllowed)
                    break;
            }
            tram.setPassengersNumber(currentPassenger+passengersCounter);
       //     System.out.println(" Pass In = "+passengersCounter + " Pass in Tram =" + tram.getPassengersNumber());
            //End Passengers in out


            // Calculate travel time
            double dwellTime = DwellTimeGenerator.generateDwellTime(passengersCounter,passengersOut);

            long travelTime = ChronoUnit.SECONDS.between(tram.getPlannedArrivalTime(),tram.getDepartureTime());
            if (travelTime<0) travelTime = -1 *travelTime;
            tram.setTravelTime(tram.getTravelTime()+travelTime+(long)dwellTime);
            // End Calculate travel time

            // Adjust stops
            Stop newNextStop = getNextStop(tram.getNextStop(), routeCSPNR, routePNRCS);

            tram.setCurrentStop(tram.getNextStop());
            tram.setNextStop(newNextStop);

            //setup departure time
            LocalTime departureTime = tram.getPlannedArrivalTime().plusSeconds((long)dwellTime);
            Event departure = new Event(2,departureTime,tram);
            if (PRINT)
                if (tram.getTramNum() == NUM||PRINT_ALL)
                    System.out.println("Arriving, Tram:"+tram.getTramNum()+" Time:"+eventTime + " travel time "+tram.getTravelTime()/60.0 + " station"+tram.getCurrentStop().getName());

            eventQueue.add(departure);
            return eventQueue;

        }
        else {
     //       System.out.println("wait");
            return eventQueue;
        }

    }

    PriorityQueue<Event>  eventHandlerDepartureStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){

        // set free and schedule new arriving
        if (!tram.getCurrentStop().getWaitingTrams().isEmpty()){
            //System.out.print("Departure, Tram:"+tram.getTramNum()+" Time:"+eventTime+" - Departure from stop  : "+tram.getCurrentStop().getName()+" - "+tram.getCurrentStop().getStopNumber());

            tram.getCurrentStop().setBusy(false);
            tram.getCurrentStop().getWaitingTrams().remove();
            if (!tram.getCurrentStop().getWaitingTrams().isEmpty()) {
                Tram waitingTram = tram.getCurrentStop().getWaitingTrams().peek();
                if (waitingTram.getPlannedArrivalTime().isBefore(eventTime.plusSeconds(40))){
                    Event arrivingEvent = new Event(1, eventTime.plusSeconds(40), waitingTram);
                    eventQueue.add(arrivingEvent);
                    waitingTram.setPlannedArrivalTime(eventTime.plusSeconds(40));
                }
            }
        }
        long drivingTime = DrivingTimeGenerator.generateDrivingTime(tram.getCurrentStop(),tram.getNextStop());
        if (PRINT)
            if (tram.getTramNum() == NUM||PRINT_ALL)
                System.out.println("Departure, Tram:"+tram.getTramNum()+" Driving time"+drivingTime/60.0+ " Station"+tram.getCurrentStop().getName());

        if (tram.getNextStop()== routeCSPNR.get(0) || tram.getNextStop()== routePNRCS.get(0) ){
            Event arrivingSwitchEvent = new Event(5, eventTime.plusSeconds(drivingTime), this.tram);
            eventQueue.add(arrivingSwitchEvent);
            /// add tram to switch queue
            EndStop eStop = (EndStop) (tram.getNextStop());
            eStop.getaSwitch().addIncomming(tram);
        }
        else {
            Event arrivingEvent = new Event(1, eventTime.plusSeconds(drivingTime), this.tram);
            eventQueue.add(arrivingEvent);
            tram.getNextStop().addTramtoWaitingTrams(tram);
        }

        tram.setPlannedArrivalTime(eventTime.plusSeconds(drivingTime));
        tram.setDepartureTime(eventTime);

        return eventQueue;
    }

    PriorityQueue<Event> eventHandlerArrivingSwitch(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){
        //System.out.println("Tram" + tram.getTramNum() + "Arrived at switch");

        if (tram.getNextStop() instanceof EndStop ){
            EndStop stop = (EndStop) tram.getNextStop();
            Switch aSwitch  = stop.getaSwitch();

            if (aSwitch.peakIncomming() == tram){
                if (aSwitch.Straight_in_busy() || aSwitch.Skewed_in_busy() || aSwitch.Skewed_out_busy()){
               //     System.out.println("Switch is busy, waiting");
                    return  eventQueue;
                }
                else{
                    if (stop.getTram_B() == null){
                    //    System.out.println("Tram" + tram.getTramNum() + "incoming, skewed");
                        Event event= new Event(6,eventTime.plusSeconds(SWITCH_SKEWED_TIME),tram);

                        if (eventTime.plusSeconds(SWITCH_SKEWED_TIME).isAfter(tram.getPlannedArrivalTime()))
                            tram.setPlannedArrivalTime(eventTime.plusSeconds(SWITCH_SKEWED_TIME));

                        eventQueue.add(event);
                        aSwitch.Set_skewed_in_busy(true);
                        stop.setTram_B(tram);
                        return eventQueue;
                    }
                    else if (stop.getTram_A() == null){
                    //    System.out.println("Tram" + tram.getTramNum() + "incoming, straight");
                        Event event= new Event(6,eventTime.plusSeconds(SWITCH_STRAIGHT_TIME),tram);

                        if (eventTime.plusSeconds(SWITCH_STRAIGHT_TIME).isAfter(tram.getPlannedArrivalTime()))
                            tram.setPlannedArrivalTime(eventTime.plusSeconds(SWITCH_STRAIGHT_TIME));
                        eventQueue.add(event);
                        aSwitch.Set_straight_in_busy(true);
                        stop.setTram_A(tram);
                        return eventQueue;
                    }
                    else{
                     //   System.out.println("Station is busy, waiting");
                        return  eventQueue;
                    }
                }
            }
            else{
           //     System.out.println("Switch is busy, waiting");
                return eventQueue;
            }
        }
        else if(tram.getCurrentStop() instanceof  EndStop){
            EndStop stop = (EndStop) tram.getCurrentStop();
            Switch aSwitch  = stop.getaSwitch();

            if (aSwitch.peakOutgoing() == tram){
                if (aSwitch.Straight_out_busy() || aSwitch.Skewed_out_busy() || aSwitch.Skewed_in_busy()){
               //     System.out.println("Switch is busy, waiting");
                    return  eventQueue;
                }
                else{
                    if (stop.getTram_A() == tram){
              //          System.out.println("Tram" + tram.getTramNum() + "outgoing, skewed");
                        Event event = new Event(6, eventTime.plusSeconds(SWITCH_SKEWED_TIME), tram);
                        tram.setPlannedArrivalTime(tram.getPlannedArrivalTime().plusSeconds(SWITCH_SKEWED_TIME));
                        eventQueue.add(event);
                        aSwitch.Set_skewed_out_busy(true);
                        stop.setTram_A(null);
                        return eventQueue;
                    }
                    else if (stop.getTram_B() == tram){
                  //      System.out.println("Tram" + tram.getTramNum() + "outgoing, straight");
                        Event event = new Event(6, eventTime.plusSeconds(SWITCH_STRAIGHT_TIME), tram);
                        tram.setPlannedArrivalTime(tram.getPlannedArrivalTime().plusSeconds(SWITCH_STRAIGHT_TIME));
                        eventQueue.add(event);
                        aSwitch.Set_straight_out_busy(true);
                        stop.setTram_B(null);
                        return eventQueue;
                    }
                    else{
                     //   System.out.println("Station is busy, waiting");
                        return  eventQueue;
                    }
                }
            }
            else{
            //    System.out.println("Switch is busy, waiting");
                return eventQueue;
            }
        }
        else{
        //    System.out.println("Error, endstop is not an endstop!");
            return eventQueue;
        }
    }

    PriorityQueue<Event> eventHandlerDepartureSwitch(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){

        if (tram.getNextStop() instanceof EndStop ){
          //  System.out.println("Tram" + tram.getTramNum() + "leaving switch to endpoint");

            EndStop stop = (EndStop) tram.getNextStop();
            Switch aSwitch  = stop.getaSwitch();

            aSwitch.Set_skewed_in_busy(false);
            aSwitch.Set_straight_in_busy(false);
            aSwitch.pollIncomming();

            if (aSwitch.peakIncomming() != null){
                Tram nextTram = aSwitch.peakIncomming();
                Event event = new Event(5, eventTime, nextTram);
                eventQueue.add(event);
            }

            if (aSwitch.peakOutgoing() != null){
                Tram nextTram = aSwitch.peakOutgoing();
                Event event = new Event(5, eventTime, nextTram);
                eventQueue.add(event);
            }

            Event event = new Event(3, eventTime, tram);
            eventQueue.add(event);
            return eventQueue;
        }
        else if (tram.getCurrentStop() instanceof EndStop){
         //   System.out.println("Tram" + tram.getTramNum() + "leaving switch to next stop");
            EndStop stop = (EndStop) tram.getCurrentStop();
            Switch aSwitch  = stop.getaSwitch();

            aSwitch.Set_skewed_out_busy(false);
            aSwitch.Set_straight_out_busy(false);
            aSwitch.pollOutgoing();

            if (aSwitch.peakIncomming() != null){
                Tram nextTram = aSwitch.peakIncomming();
                Event event = new Event(5, eventTime, nextTram);
                eventQueue.add(event);
            }

            if (aSwitch.peakOutgoing() != null){
                Tram nextTram = aSwitch.peakOutgoing();
                Event event = new Event(5, eventTime, nextTram);
                eventQueue.add(event);
            }

            long drivingTime = DrivingTimeGenerator.generateDrivingTime(tram.getCurrentStop(),tram.getNextStop());

            if (PRINT)
                if (tram.getTramNum() ==NUM||PRINT_ALL)
                    System.out.println("Departure Switch, Tram:"+tram.getTramNum()+" Driving time "+drivingTime+ " Station"+tram.getCurrentStop().getName());

            // System.out.println(" Driving time"+drivingTime);

            tram.setPlannedArrivalTime(eventTime.plusSeconds(drivingTime));
            tram.getNextStop().addTramtoWaitingTrams(tram);

            Event arrivingEvent = new Event(1, eventTime.plusSeconds(drivingTime), tram);
            eventQueue.add(arrivingEvent);
            return eventQueue;
        }
        else{
         //   System.out.println("Error, leaving the switch!");
            return eventQueue;
        }
    }


    PriorityQueue<Event> eventHandlerArrivingEndStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS) throws IOException {

            if (!tram.getPlannedArrivalTime().equals(eventTime)){
                if (tram.getPlannedArrivalTime().isAfter(eventTime)){

                    Event event = new Event(3,tram.getPlannedArrivalTime(),tram);
                    eventQueue.add(event);
                    return eventQueue;
                }

            }

            // Passengers in out
            int passengersOut =tram.getPassengersNumber();

            int currentPassenger = tram.getPassengersNumber()-passengersOut;
            if(currentPassenger<0) { currentPassenger =0;
            //System.out.print(" Pass Out =" + tram.getPassengersNumber());
            }
            else{
                //System.out.print(" Pass Out =" + passengersOut);
            }


            int numPassAllowed = tram.getMaxCapacity() - currentPassenger;

            boolean withinCalcTime = false;
            if (eventTime.isBefore(CALCULATION_END_TIME) && eventTime.isAfter(CALCULATION_START_TIME))
                withinCalcTime = true;

            /// passenger getting in
            int passengersCounter = 0;
            Stop currStop = tram.getNextStop();

            Queue<Passenger> passengerQueue= tram.getNextStop().getPassengerQueue();
            while (!passengerQueue.isEmpty()){
                if (withinCalcTime){
                    currStop.updateWaitingTime(passengerQueue.remove(),eventTime);
                }
                else
                    passengerQueue.remove();
                passengersCounter++;
                if (passengersCounter>numPassAllowed)
                    break;
            }
            tram.setPassengersNumber(currentPassenger+passengersCounter);

     //       System.out.println(" Pass In = "+passengersCounter + " Pass in Tram =" + tram.getPassengersNumber());
            //End Passengers in

            // Calculate travel time and dwell time for OUT ONLY
            long travelTime = ChronoUnit.SECONDS.between(tram.getPlannedArrivalTime(),tram.getDepartureTime());
           // System.out.println("tram.getPlannedArrivalTime()"+tram.getPlannedArrivalTime()+"+tram.getDepartureTime() "+tram.getDepartureTime());
            if (travelTime<0) travelTime = -1 *travelTime;

            if (eventTime.isBefore(LocalTime.of(6,20,0)))
            {
                tram.setTravelTime(tram.getTravelTime()+travelTime);
            }
            else{
            tram.setTravelTime(tram.getTravelTime()+travelTime+TURN_AROUND_TIME);
            }
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

            LocalTime possibleDeparture =  getEventTime().plusSeconds(0);


            if (eventTime.isAfter(LocalTime.of(6,20,0)))
                possibleDeparture =  getEventTime().plusSeconds(TURN_AROUND_TIME);



            if (eventTime.equals(LocalTime.of(6,0,0)))
                possibleDeparture =  getEventTime().plusSeconds(0);


            Event departure;
            if (PRINT)
            if (tram.getTramNum() ==NUM || PRINT_ALL) {
                System.out.println("--------------------------------");
                System.out.println("Arriving " + tram.getCurrentStop().getName() + "  STOP, Tram:" + tram.getTramNum() + " Time:" + eventTime);
                System.out.println("Travel time " + tram.getTravelTime() / 60.0);
                System.out.println("possible departure " + possibleDeparture + " plannedDepartureEndStop " + plannedDepartureEndStop);
                System.out.println("--------------------------------");
            }
            tram.setTravelTime(0);

            if (possibleDeparture.isAfter(plannedDepartureEndStop)) {
                tram.setDepartureTime(possibleDeparture);
                departure = new Event(4,possibleDeparture,tram);
                if (eventTime.isBefore(CALCULATION_END_TIME) && eventTime.isAfter(CALCULATION_START_TIME))
                    endStop.addDepartureDelay(ChronoUnit.SECONDS.between(plannedDepartureEndStop,possibleDeparture));

            }
            else {
                tram.setDepartureTime(plannedDepartureEndStop);
                departure = new Event(4, plannedDepartureEndStop, tram);
                if (eventTime.isBefore(CALCULATION_END_TIME) && eventTime.isAfter(CALCULATION_START_TIME))
                    endStop.addDepartureDelay((long)0);
            }


            eventQueue.add(departure);
            return eventQueue;
    }

    PriorityQueue<Event> eventHandlerDepartureEndStop(PriorityQueue<Event> eventQueue,List<Stop> routeCSPNR ,List<Stop> routePNRCS){
        // set free and schedule new arriving
        if (!tram.getCurrentStop().getWaitingTrams().isEmpty()){
            if (PRINT)
                if (tram.getTramNum() == NUM||PRINT_ALL)
                    System.out.print("Departure, Tram:"+tram.getTramNum()+" Time:"+eventTime+" - Departure from stop : "+tram.getCurrentStop().getName()+" - "+tram.getCurrentStop().getStopNumber());

            tram.getCurrentStop().setBusy(false);
            tram.getCurrentStop().getWaitingTrams().remove();
            if (!tram.getCurrentStop().getWaitingTrams().isEmpty()) {
                Tram waitingTram = tram.getCurrentStop().getWaitingTrams().peek();
                if (waitingTram.getPlannedArrivalTime().isBefore(eventTime.plusSeconds(40))){
                    Event arrivingEvent = new Event(5, eventTime.plusSeconds(40), waitingTram);
                    eventQueue.add(arrivingEvent);
                    waitingTram.setPlannedArrivalTime(eventTime.plusSeconds(40));
                }
            }
        }
        tram.setDepartureTime(eventTime);

        EndStop eStop = (EndStop) (tram.getCurrentStop());
        eStop.getaSwitch().addOutgoing(tram);

        Event arrivingEvent = new Event(5, eventTime, tram);
        eventQueue.add(arrivingEvent);
        return eventQueue;
    }


    public int getEventType() {
        return eventType;
    }

    public LocalTime getEventTime() {
        return eventTime;
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
