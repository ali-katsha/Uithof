import entities.EndStop;
import entities.Stop;
import entities.Tram;
import events.Event;
import generators.PassengersArrivingGenerator;
import generators.TimeTableGenerator;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here


        LocalTime simulationClock = LocalTime.of(0, 0, 0);
        LocalTime simulationStartTime = LocalTime.of(6, 0, 0);
        LocalTime simulationEndTime = LocalTime.of(11, 0, 0);

        long maxWaitingTime;
        long totalWaitingTime;
        long numWaitPassenger;


        List<Integer> departureDelayCS;
        List<Integer> departureDelayPNR;

        List<Integer> arrivingDelayCS;
        List<Integer> arrivingDelayPNR;





        EndStop CSStop =  new EndStop("CS",1);



        Stop stopA1 = new Stop( "Vaartsche Rijn", 0,2 ) ;
        Stop stopA2 = new Stop(  "Galgenwaard", 0 ,3);
        Stop stopA3 = new Stop(  "De Kromme Rijn", 0 ,4);
        Stop stopA4 = new Stop( "Padualaan", 0 ,5 ) ;
        Stop stopA5 = new Stop(  "Heidelberglaan", 0 ,6);
        Stop stopA6 = new Stop( "UMC", 0 ,7 ) ;
        Stop stopA7 = new Stop(  "WKZ", 0 ,8 );

        EndStop PNRStop =  new EndStop("PNR",9);

        Stop stopB7 = new Stop( "Vaartsche Rijn", 0 ,17) ;
        Stop stopB6 = new Stop(  "Galgenwaard", 0,16 );
        Stop stopB5 = new Stop(  "De Kromme Rijn", 0 ,15);
        Stop stopB4 = new Stop( "Padualaan", 0 ,14) ;
        Stop stopB3 = new Stop(  "Heidelberglaan", 0 ,13);
        Stop stopB2 = new Stop( "UMC", 0 ,12) ;
        Stop stopB1 = new Stop(  "WKZ", 0 ,11);


        int frequency = 2;
        int turnaroundTime;

        // calculate timetable
        TimeTableGenerator.generateTimeTable(CSStop, simulationStartTime,simulationEndTime.plusHours(1),frequency,0);
        TimeTableGenerator.generateTimeTable(PNRStop, simulationStartTime,simulationEndTime.plusHours(1),frequency,15);

        List<Stop> routeCSPNR = new ArrayList<Stop>();
        List<Stop> routePNRCS = new ArrayList<Stop>();
        List<Stop> stopList = new ArrayList<>();

        routeCSPNR.add(CSStop);
        routeCSPNR.add(stopA1);
        routeCSPNR.add(stopA2);
        routeCSPNR.add(stopA3);
        routeCSPNR.add(stopA4);
        routeCSPNR.add(stopA5);
        routeCSPNR.add(stopA6);
        routeCSPNR.add(stopA7);
        routeCSPNR.add(PNRStop);


        routePNRCS.add(PNRStop);
        routePNRCS.add(stopB1);
        routePNRCS.add(stopB2);
        routePNRCS.add(stopB3);
        routePNRCS.add(stopB4);
        routePNRCS.add(stopB5);
        routePNRCS.add(stopB6);
        routePNRCS.add(stopB7);
        routePNRCS.add(CSStop);



        stopList.add(CSStop);
        stopList.add(stopA1);
        stopList.add(stopA2);
        stopList.add(stopA3);
        stopList.add(stopA4);
        stopList.add(stopA5);
        stopList.add(stopA6);
        stopList.add(stopA7);
        stopList.add(PNRStop);
        stopList.add(stopB1);
        stopList.add(stopB2);
        stopList.add(stopB3);
        stopList.add(stopB4);
        stopList.add(stopB5);
        stopList.add(stopB6);
        stopList.add(stopB7);




        PriorityQueue<Event> eventQueue = new PriorityQueue<>();

        for(int i=0;i<1;i++) {
            Tram tram = new Tram();
            tram.setNextStop(CSStop);
            //tram.setNextStop(stopA1);
            tram.setTramNum(i);
            tram.setDepartureTime(simulationStartTime);
            tram.setPlannedArrivalTime(simulationStartTime);
            tram.setDirection(0);
            Event arriving = new Event(3, simulationStartTime, tram);
            CSStop.addTramtoWaitingTrams(tram);
            eventQueue.add(arriving);
        }


        for(int i=0;i<1;i++) {
            Tram tram = new Tram();
            tram.setNextStop(PNRStop);
            //tram.setNextStop(stopA1);
            tram.setTramNum(i+4);
            tram.setDepartureTime(simulationStartTime);
            tram.setPlannedArrivalTime(simulationStartTime);
            tram.setDirection(0);
            Event arriving = new Event(3, simulationStartTime, tram);
            PNRStop.addTramtoWaitingTrams(tram);
            eventQueue.add(arriving);
        }


/*
        Tram tram1 = new Tram();
        tram1.setNextStop(CSStop);
        //tram.setNextStop(stopA1);
        tram1.setTramNum(1);
        tram1.setDepartureTime(simulationStartTime.plusMinutes(2));
        tram1.setPlannedArrivalTime(simulationStartTime.plusMinutes(2));
        Event arriving1 = new Event(1,simulationStartTime.plusMinutes(2),tram1);
        CSStop.addTramtoWaitingTrams(tram1);
        eventQueue.add(arriving1);

*/




        Event eventPassengerArrival = new Event(9,simulationStartTime,CSStop);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopA1);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopA2);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopA3);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopA4);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopA5);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopA6);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopA7);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,PNRStop);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopB1);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopB2);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopB3);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopB4);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopB5);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopB6);
        eventQueue.add(eventPassengerArrival);
        eventPassengerArrival = new Event(9,simulationStartTime,stopB7);
        eventQueue.add(eventPassengerArrival);




        while (true){
            Event event = eventQueue.remove();
            simulationClock = event.getEventTime();
            eventQueue = event.eventHandler(eventQueue,routeCSPNR,routePNRCS);
            if(event.getEventTime().isAfter(simulationEndTime)){
                System.out.println("A");
                break;
            }
        }
        System.out.println("------------Waiting in Stops----------");
        for (int i =0 ; i<stopList.size();i++){
            System.out.println("Stop" + stopList.get(i).getStopNumber()+" -"+stopList.get(i).getName());
            System.out.println("Max waiting : " + stopList.get(i).getMaxWaitingTime());
            System.out.println(" # Passengers waiting : " + stopList.get(i).getNumWaitPassenger());
            System.out.println("Max waiting : " + stopList.get(i).getTotalWaitingTime());

        }
        System.out.println(1);

        // Testing
        /*
        PassengersArrivingGenerator p = new PassengersArrivingGenerator();
        System.out.println(p.getNumPassengers(CSStop,LocalTime.of(6,15,0),"in"));
        System.out.println(p.getNumPassengers(CSStop,LocalTime.of(7,15,0),"in"));
        System.out.println(p.getNumPassengers(CSStop,LocalTime.of(9,15,0),"in"));
        System.out.println(p.getNumPassengers(CSStop,LocalTime.of(16,15,0),"in"));
        System.out.println(p.getNumPassengers(CSStop,LocalTime.of(18,15,0),"in"));
        */

        System.out.println(numberOfTrams(4));
    }

    public  static int numberOfTrams(int frequency)
    {

        if(60/frequency<=22)
        {
            double num1= 60/frequency;
            double num2=22/num1;
            double num3=num2*2;
            return (int)Math.ceil(num3);

        }
        else {

            return 2;
        }
    }
}
