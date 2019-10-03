import entities.EndStop;
import entities.Stop;
import events.Event;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {
	// write your code here

        LocalTime simulationClock= new LocalTime(6,0, 0 , 0 );


        int maxWaitingTime;
        int totalWaitingTime;
        int numWaitPassenger;


        List<Integer> departureDelayCS;
        List<Integer> departureDelayPNR;

        List<Integer> arrivingDelayCS;
        List<Integer> arrivingDelayPNR;

        PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();


        int frequency;
        int turnaroundTime;
        // calculate timetable


        EndStop PNRStop =  new EndStop("PNR");
        EndStop CSStop =  new EndStop("CSß");

        List<Stop> routePNRCS = new ArrayList<Stop>();
        List<Stop> routeCSPNR = new ArrayList<Stop>();




    }
}
