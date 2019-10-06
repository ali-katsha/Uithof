import entities.EndStop;
import entities.Stop;
import events.Event;
import generators.PassengersArrivingGenerator;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here


        LocalTime simulationClock = LocalTime.of(0, 0, 0);
        LocalTime simulationStartTime = LocalTime.of(6, 0, 0);
        LocalTime simulationEndTime = LocalTime.of(21, 30, 0);

        long maxWaitingTime;
        long totalWaitingTime;
        long numWaitPassenger;


        List<Integer> departureDelayCS;
        List<Integer> departureDelayPNR;

        List<Integer> arrivingDelayCS;
        List<Integer> arrivingDelayPNR;




        int frequency;
        int turnaroundTime;
        // calculate timetable


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



        List<Stop> routeCSPNR = new ArrayList<Stop>();
        List<Stop> routePNRCS = new ArrayList<Stop>();

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



        PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();

        // Testing
        PassengersArrivingGenerator p = new PassengersArrivingGenerator();
        System.out.println(p.getNumPassengers(CSStop,LocalTime.of(7,15,0),"in"));

    }
}
