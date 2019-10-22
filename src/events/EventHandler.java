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

public class EventHandler{

    private Queue<Event> eventQueue;
    private List<Stop> routeCSPNR;
    private List<Stop> routePNRCS;

    private static final LocalTime CALCULATION_START_TIME = LocalTime.of(7, 0, 0);
    private static final LocalTime CALCULATION_END_TIME = LocalTime.of(19, 0, 0);

    public static final int Frequency = 2;
    public static final long TURN_AROUND_TIME = 240;

    private static final int SWITCH_STRAIGHT_TIME = 0;
    private static final int SWITCH_SKEWED_TIME = 60;

    private static final int NUM = 10;
    private static final boolean PRINT = false;
    private static final boolean PRINT_ALL = false;

    public EventHandler(List<Stop> routeCSPNR , List<Stop> routePNRCS) {
        eventQueue = new PriorityQueue<Event>();
        this.routeCSPNR = routeCSPNR;
        this.routePNRCS = routePNRCS;
    }

    public void addEvent(Event event){
        eventQueue.add(event);
    }

    /* 1 : arrival at intermediate  stop , 2 : departure from intermediate stop, 3- Arriving at endpoint, 4-departure at end stop
     * 5- arrival at switch , 6- departure from switch , 7-passenger arrival
     * */

    public LocalTime  HandleEvent() throws IOException  {
        Event event = eventQueue.poll();
        if (event == null)
            return null;

        switch (event.eventType) {
            case 1:
                arrivingAtStop((TramEvent) event);
                break;
            case 2:
                departureFromStop((TramEvent) event);
                break;
            case 3:
                arrivingAtEndStop((TramEvent) event);
                break;
            case 4:
                DepartureFromEndStop((TramEvent) event);
                break;
            case 5:
                ArrivingAtSwitch((TramEvent) event);
                break;
            case 6:
                DepartureFromSwitch((TramEvent) event);
                break;
            case 7:
                passengerArrival((PassengerEvent) event);
                break;
        }

        return event.eventTime;
    }

    private void passengerArrival(PassengerEvent event) throws IOException {
        Stop stop = event.stop;
        LocalTime eventTime = event.eventTime;
        int numPass = PassengersArrivingGenerator.getNumPassengers(stop,eventTime);

        for (int i=0;i<numPass;i++){
            stop.getPassengerQueue().add(new Passenger(eventTime,stop));
        }

        PassengerEvent nextArrivingEvent = new PassengerEvent(7, eventTime.plusMinutes(15), stop);
        eventQueue.add(nextArrivingEvent);
    }

    private void arrivingAtStop(TramEvent event) throws IOException {
        Tram tram = event.tram;
        LocalTime eventTime = event.eventTime;
        Stop stop = tram.getNextStop();

        if (stop.getWaitingTrams().peek() == tram){

            // Check for delayed arrival
            if (!tram.getPlannedArrivalTime().equals(eventTime)){
                if (tram.getPlannedArrivalTime().isAfter(eventTime)){
                    TramEvent newArrival = new TramEvent(1,tram.getPlannedArrivalTime(),tram);
                    eventQueue.add(newArrival);
                    return;
                }
            }

            //stop busy
            stop.setBusy(true);

            // Passengers in out
            int passengersOut = PassengersOutGenerator.getNumPassengers(tram,stop,eventTime);
            int currentPassenger = tram.getPassengersNumber()-passengersOut;
            int numPassAllowed = tram.getMaxCapacity() - currentPassenger;
            int passengersCounter = 0;

            boolean withinCalcTime = false;
            if (eventTime.isBefore(CALCULATION_END_TIME) && eventTime.isAfter(CALCULATION_START_TIME)) withinCalcTime = true;

            Queue<Passenger> passengerQueue= stop.getPassengerQueue();
            while (!passengerQueue.isEmpty()){
                if (withinCalcTime){
                    stop.updateWaitingTime(passengerQueue.remove(),eventTime);
                }
                else
                    passengerQueue.remove();

                passengersCounter++;
                if (passengersCounter>numPassAllowed)
                    break;
            }

            tram.setPassengersNumber(currentPassenger+passengersCounter);

            // Calculate travel time
            double dwellTime = DwellTimeGenerator.generateDwellTime(passengersCounter,passengersOut);
            long travelTime = ChronoUnit.SECONDS.between(tram.getPlannedArrivalTime(),tram.getDepartureTime());
            if (travelTime<0) travelTime = -1 *travelTime;
            tram.setTravelTime(tram.getTravelTime()+travelTime+(long)dwellTime);

            // Adjust stops
            Stop nextStop = getNextStop(stop);
            tram.setCurrentStop(stop);
            tram.setNextStop(nextStop);

            //setup departure time
            LocalTime departureTime = tram.getPlannedArrivalTime().plusSeconds((long)dwellTime);
            TramEvent departure = new TramEvent(2,departureTime,tram);

            if (PRINT)
                if (tram.getTramNum() == NUM||PRINT_ALL)
                    System.out.println("Arriving, Tram:"+tram.getTramNum()+" Time:"+eventTime + " travel time "+tram.getTravelTime()/60.0 + " station"+tram.getCurrentStop().getName());

            eventQueue.add(departure);
        }
    }

    private void departureFromStop(TramEvent event){
        Tram tram = event.tram;
        LocalTime eventTime = event.eventTime;
        Stop stop = tram.getCurrentStop();

        // set free and schedule new arriving
        stop.setBusy(false);
        stop.getWaitingTrams().remove();

        if (!stop.getWaitingTrams().isEmpty()) {
            Tram waitingTram = stop.getWaitingTrams().peek();
            if (waitingTram.getPlannedArrivalTime().isBefore(eventTime.plusSeconds(40))) {
                TramEvent arrivingEvent = new TramEvent(1, eventTime.plusSeconds(40), waitingTram);
                eventQueue.add(arrivingEvent);
                waitingTram.setPlannedArrivalTime(eventTime.plusSeconds(40));
            }
        }

        long drivingTime = DrivingTimeGenerator.generateDrivingTime(stop,tram.getNextStop());

        if (PRINT)
            if (tram.getTramNum() == NUM||PRINT_ALL)
                System.out.println("Departure, Tram:"+tram.getTramNum()+" Driving time"+drivingTime/60.0+ " Station"+tram.getCurrentStop().getName());

        if (tram.getNextStop()== routeCSPNR.get(0) || tram.getNextStop()== routePNRCS.get(0) ){
            TramEvent arrivingSwitchEvent = new TramEvent(5, eventTime.plusSeconds(drivingTime), tram);
            eventQueue.add(arrivingSwitchEvent);
            EndStop eStop = (EndStop) (tram.getNextStop());
            eStop.getaSwitch().addIncomming(tram);
        }
        else {
            TramEvent arrivingEvent = new TramEvent(1, eventTime.plusSeconds(drivingTime), tram);
            eventQueue.add(arrivingEvent);
            tram.getNextStop().addTramtoWaitingTrams(tram);
        }

        tram.setPlannedArrivalTime(eventTime.plusSeconds(drivingTime));
        tram.setDepartureTime(eventTime);
    }

    private void ArrivingAtSwitch(TramEvent event){
        Tram tram = event.tram;
        LocalTime eventTime = event.eventTime;

        if (tram.getNextStop() instanceof EndStop ){
            EndStop stop = (EndStop) tram.getNextStop();
            Switch aSwitch  = stop.getaSwitch();

            if (aSwitch.peakIncomming() == tram){
                if (!aSwitch.Straight_in_busy() && !aSwitch.Skewed_in_busy() & !aSwitch.Skewed_out_busy()){
                    if (stop.getTram_B() == null){
                        TramEvent switchEvent= new TramEvent(6,eventTime.plusSeconds(SWITCH_SKEWED_TIME),tram);

                        if (eventTime.plusSeconds(SWITCH_SKEWED_TIME).isAfter(tram.getPlannedArrivalTime()))
                            tram.setPlannedArrivalTime(eventTime.plusSeconds(SWITCH_SKEWED_TIME));

                        eventQueue.add(switchEvent);
                        aSwitch.Set_skewed_in_busy(true);
                        stop.setTram_B(tram);
                    }
                    else if (stop.getTram_A() == null){
                        TramEvent switchEvent= new TramEvent(6,eventTime.plusSeconds(SWITCH_STRAIGHT_TIME),tram);

                        if (eventTime.plusSeconds(SWITCH_STRAIGHT_TIME).isAfter(tram.getPlannedArrivalTime()))
                            tram.setPlannedArrivalTime(eventTime.plusSeconds(SWITCH_STRAIGHT_TIME));

                        eventQueue.add(switchEvent);
                        aSwitch.Set_straight_in_busy(true);
                        stop.setTram_A(tram);
                    }
                }
            }
        }
        else if(tram.getCurrentStop() instanceof  EndStop){
            EndStop stop = (EndStop) tram.getCurrentStop();
            Switch aSwitch  = stop.getaSwitch();

            if (aSwitch.peakOutgoing() == tram){
                if (!aSwitch.Straight_out_busy() && !aSwitch.Skewed_out_busy() && !aSwitch.Skewed_in_busy()){
                    if (stop.getTram_A() == tram){
                        TramEvent switchEvent = new TramEvent(6, eventTime.plusSeconds(SWITCH_SKEWED_TIME), tram);
                        tram.setPlannedArrivalTime(tram.getPlannedArrivalTime().plusSeconds(SWITCH_SKEWED_TIME));
                        eventQueue.add(switchEvent);
                        aSwitch.Set_skewed_out_busy(true);
                        stop.setTram_A(null);
                    }
                    else if (stop.getTram_B() == tram){
                        TramEvent switchEvent = new TramEvent(6, eventTime.plusSeconds(SWITCH_STRAIGHT_TIME), tram);
                        tram.setPlannedArrivalTime(tram.getPlannedArrivalTime().plusSeconds(SWITCH_STRAIGHT_TIME));
                        eventQueue.add(switchEvent);
                        aSwitch.Set_straight_out_busy(true);
                        stop.setTram_B(null);
                    }
                }
            }
        }
    }

    private void DepartureFromSwitch(TramEvent event){
        Tram tram = event.tram;
        LocalTime eventTime = event.eventTime;

        if (tram.getNextStop() instanceof EndStop ){
            EndStop stop = (EndStop) tram.getNextStop();
            Switch aSwitch  = stop.getaSwitch();

            aSwitch.Set_skewed_in_busy(false);
            aSwitch.Set_straight_in_busy(false);
            aSwitch.pollIncomming();

            if (aSwitch.peakIncomming() != null){
                Tram nextTram = aSwitch.peakIncomming();
                TramEvent switchEvent = new TramEvent(5, eventTime, nextTram);
                eventQueue.add(switchEvent);
            }

            if (aSwitch.peakOutgoing() != null){
                Tram nextTram = aSwitch.peakOutgoing();
                TramEvent switchEvent = new TramEvent(5, eventTime, nextTram);
                eventQueue.add(switchEvent);
            }

            TramEvent arivalEvent = new TramEvent(3, eventTime, tram);
            eventQueue.add(arivalEvent);
        }
        else if (tram.getCurrentStop() instanceof EndStop){
            EndStop stop = (EndStop) tram.getCurrentStop();
            Switch aSwitch  = stop.getaSwitch();

            aSwitch.Set_skewed_out_busy(false);
            aSwitch.Set_straight_out_busy(false);
            aSwitch.pollOutgoing();

            if (aSwitch.peakIncomming() != null){
                Tram nextTram = aSwitch.peakIncomming();
                TramEvent switchEvent = new TramEvent(5, eventTime, nextTram);
                eventQueue.add(switchEvent);
            }

            if (aSwitch.peakOutgoing() != null){
                Tram nextTram = aSwitch.peakOutgoing();
                TramEvent switchEvent = new TramEvent(5, eventTime, nextTram);
                eventQueue.add(switchEvent);
            }

            long drivingTime = DrivingTimeGenerator.generateDrivingTime(stop,tram.getNextStop());
            if (PRINT)
                if (tram.getTramNum() ==NUM||PRINT_ALL)
                    System.out.println("Departure Switch, Tram:"+tram.getTramNum()+" Driving time "+drivingTime+ " Station"+tram.getCurrentStop().getName());

            tram.setPlannedArrivalTime(eventTime.plusSeconds(drivingTime));
            tram.getNextStop().addTramtoWaitingTrams(tram);

            TramEvent arrivingEvent = new TramEvent(1, eventTime.plusSeconds(drivingTime), tram);
            eventQueue.add(arrivingEvent);
        }
    }

    private void arrivingAtEndStop(TramEvent event) throws IOException {
            Tram tram = event.tram;
            LocalTime eventTime = event.eventTime;
            EndStop stop = (EndStop) tram.getNextStop();

            if (!tram.getPlannedArrivalTime().equals(eventTime)){
                if (tram.getPlannedArrivalTime().isAfter(eventTime)){
                    TramEvent arivalEvent = new TramEvent(3,tram.getPlannedArrivalTime(),tram);
                    eventQueue.add(arivalEvent);
                    return;
                }
            }

            // Passengers in out
            int passengersOut =tram.getPassengersNumber();

            int currentPassenger = tram.getPassengersNumber()-passengersOut;
            if(currentPassenger<0) currentPassenger =0;
            int numPassAllowed = tram.getMaxCapacity() - currentPassenger;

            boolean withinCalcTime = false;
            if (eventTime.isBefore(CALCULATION_END_TIME) && eventTime.isAfter(CALCULATION_START_TIME))
                withinCalcTime = true;

            /// passenger getting in
            int passengersCounter = 0;
            Queue<Passenger> passengerQueue= stop.getPassengerQueue();
            
            while (!passengerQueue.isEmpty()){
                if (withinCalcTime){
                    stop.updateWaitingTime(passengerQueue.remove(),eventTime);
                }
                else
                    passengerQueue.remove();
                passengersCounter++;
                if (passengersCounter>numPassAllowed)
                    break;
            }
            tram.setPassengersNumber(currentPassenger+passengersCounter);

            // Calculate travel time and dwell time for OUT ONLY
            long travelTime = ChronoUnit.SECONDS.between(tram.getPlannedArrivalTime(),tram.getDepartureTime());
            if (travelTime<0) travelTime = -1 *travelTime;

            if (eventTime.isBefore(LocalTime.of(6,20,0)))
            {
                tram.setTravelTime(tram.getTravelTime()+travelTime);
            }
            else{
            tram.setTravelTime(tram.getTravelTime()+travelTime+TURN_AROUND_TIME);
            }

            // Adjust stops
            Stop nextStop = getNextStop(stop);
            tram.setCurrentStop(stop);
            tram.setNextStop(nextStop);

            // Get the time table and set the
            EndStop endStop = (EndStop) tram.getCurrentStop();
            LocalTime plannedDepartureEndStop = endStop.getPlannedDeparture().get(0);
            LocalTime plannedArrivalEndStop = endStop.getPlannedArrival().get(0);
            endStop.getPlannedArrival().remove(0);
            endStop.getPlannedDeparture().remove(0);

            tram.setPlannedArrivalEndStop(plannedArrivalEndStop);
            tram.setPlannedDepartureEndStop(plannedDepartureEndStop);
            LocalTime possibleDeparture =  eventTime.plusSeconds(0);

            if (eventTime.isAfter(LocalTime.of(6,20,0)))
                possibleDeparture =  eventTime.plusSeconds(TURN_AROUND_TIME);

            if (eventTime.equals(LocalTime.of(6,0,0)))
                possibleDeparture =  eventTime.plusSeconds(0);

            TramEvent departure;
            if (PRINT){
                if (tram.getTramNum() ==NUM || PRINT_ALL) {
                    System.out.println("--------------------------------");
                    System.out.println("Arriving " + tram.getCurrentStop().getName() + "  STOP, Tram:" + tram.getTramNum() + " Time:" + eventTime);
                    System.out.println("Travel time " + tram.getTravelTime() / 60.0);
                    System.out.println("possible departure " + possibleDeparture + " plannedDepartureEndStop " + plannedDepartureEndStop);
                    System.out.println("--------------------------------");
                }
            }
            tram.setTravelTime(0);

            if (possibleDeparture.isAfter(plannedDepartureEndStop)) {
                tram.setDepartureTime(possibleDeparture);
                departure = new TramEvent(4,possibleDeparture,tram);
                if (eventTime.isBefore(CALCULATION_END_TIME) && eventTime.isAfter(CALCULATION_START_TIME))
                    endStop.addDepartureDelay(ChronoUnit.SECONDS.between(plannedDepartureEndStop,possibleDeparture));

            }
            else {
                tram.setDepartureTime(plannedDepartureEndStop);
                departure = new TramEvent(4, plannedDepartureEndStop, tram);
                if (eventTime.isBefore(CALCULATION_END_TIME) && eventTime.isAfter(CALCULATION_START_TIME))
                    endStop.addDepartureDelay((long)0);
            }

            eventQueue.add(departure);
    }

    private void DepartureFromEndStop(TramEvent event){
        Tram tram = event.tram;
        LocalTime eventTime = event.eventTime;
        EndStop stop = (EndStop) tram.getCurrentStop();

        if (!stop.getWaitingTrams().isEmpty()){
            if (PRINT)
                if (tram.getTramNum() == NUM||PRINT_ALL)
                    System.out.print("Departure, Tram:"+tram.getTramNum()+" Time:"+eventTime+" - Departure from stop : "+tram.getCurrentStop().getName()+" - "+tram.getCurrentStop().getStopNumber());

            stop.setBusy(false);
            stop.getWaitingTrams().remove();
            if (!stop.getWaitingTrams().isEmpty()) {
                Tram waitingTram = stop.getWaitingTrams().peek();
                if (waitingTram.getPlannedArrivalTime().isBefore(eventTime.plusSeconds(40))){
                    TramEvent arrivingEvent = new TramEvent(5, eventTime.plusSeconds(40), waitingTram);
                    eventQueue.add(arrivingEvent);
                    waitingTram.setPlannedArrivalTime(eventTime.plusSeconds(40));
                }
            }
        }

        tram.setDepartureTime(eventTime);
        stop.getaSwitch().addOutgoing(tram);

        TramEvent arrivingEvent = new TramEvent(5, eventTime, tram);
        eventQueue.add(arrivingEvent);
    }

    private Stop getNextStop(Stop stop){
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
}
