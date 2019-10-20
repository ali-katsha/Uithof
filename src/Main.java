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
    private static final int TURN_AROUND_TIME_MINUTES = 4;

    public static void main(String[] args) throws IOException {
        int frequency = 12;
        int numRuns = 1;

        double globalPercentageWaiting=0;
        long globalMaxDelay = 0;
        long globalMaxWaiting =0;
        double globalAvgWaiting = 0 ;

        for(int indexRun = 0;indexRun <numRuns;indexRun++){

                System.out.println("*********************** Run"+ indexRun +" *********************");
                //vars
                LocalTime simulationClock = LocalTime.of(0, 0, 0);
                LocalTime simulationStartTime = LocalTime.of(6, 0, 0);
                LocalTime simulationEndTime = LocalTime.of(22, 0, 0);
                EndStop CSStop =  new EndStop("CS",1);
                EndStop PNRStop =  new EndStop("PNR",9);
                List<Stop> routeCSPNR = new ArrayList<Stop>();
                List<Stop> routePNRCS = new ArrayList<Stop>();
                List<Stop> stopList = new ArrayList<>();

                // calculate timetable
                TimeTableGenerator.generateTimeTable(CSStop, simulationStartTime,simulationEndTime.plusHours(1),frequency,2,TURN_AROUND_TIME_MINUTES);
                TimeTableGenerator.generateTimeTable(PNRStop, simulationStartTime,simulationEndTime.plusHours(1),frequency,0,TURN_AROUND_TIME_MINUTES);

                //init events and lists
                PriorityQueue<Event> eventQueue = new PriorityQueue<>();
                initStops( routeCSPNR,routePNRCS, stopList, CSStop,PNRStop);
                initTramEvents(eventQueue, frequency, CSStop,  PNRStop, simulationStartTime);
                initArrivingPassengersEvents(eventQueue,stopList,simulationStartTime);


                while (true){
                    Event event = eventQueue.remove();
                    simulationClock = event.getEventTime();
                    eventQueue = event.eventHandler(eventQueue,routeCSPNR,routePNRCS);
                    if(event.getEventTime().isAfter(simulationEndTime)){
                        System.out.println("Simulation Ended");
                        break;
                    }
                }
                System.out.println("------------Stats----------");
                long totalNumSimulationWaiting =0;
                long totalSimulationWaiting =0;
                long maxWaiting =0;

                for (int i =0 ; i<stopList.size();i++){
               //     System.out.println("Stop" + stopList.get(i).getStopNumber()+" -"+stopList.get(i).getName());
              //      System.out.println("Max waiting : " + stopList.get(i).getMaxWaitingTime());
                //    System.out.println(" # Passengers waiting : " + stopList.get(i).getNumWaitPassenger());
                  //  System.out.println(" waiting time : " + stopList.get(i).getTotalWaitingTime());
                    totalNumSimulationWaiting += stopList.get(i).getNumWaitPassenger();
                    totalSimulationWaiting +=stopList.get(i).getTotalWaitingTime();
                    if (stopList.get(i).getMaxWaitingTime()>maxWaiting) maxWaiting=stopList.get(i).getMaxWaitingTime();

                }
                double avgWaiting = (double)totalSimulationWaiting/(double)totalNumSimulationWaiting;
                System.out.println("AVG Waiting :"+ avgWaiting);
                System.out.println("MAx Waiting :"+ maxWaiting);

                int numTrainDelay = 0;
                long maxDelay = 0;
                // Testing
                for (int i=0;i<PNRStop.getDepartureDelayList().size();i++){
                 //   System.out.println(PNRStop.getDepartureDelayList().get(i));
                    if (PNRStop.getDepartureDelayList().get(i) > 60)
                        numTrainDelay++;
                    if (PNRStop.getDepartureDelayList().get(i) > maxDelay) maxDelay =PNRStop.getDepartureDelayList().get(i);

                }

                for (int i=0;i<CSStop.getDepartureDelayList().size();i++){
               //     System.out.println(CSStop.getDepartureDelayList().get(i));
                    if (CSStop.getDepartureDelayList().get(i) > 60)
                        numTrainDelay++;
                    if (CSStop.getDepartureDelayList().get(i) > maxDelay) maxDelay=CSStop.getDepartureDelayList().get(i);
                }
                int s = (CSStop.getDepartureDelayList().size()+PNRStop.getDepartureDelayList().size());
                double percentageWaiting = (double) numTrainDelay /(double) s;
                System.out.println("Percentage of trams delayed more than a minute :" + percentageWaiting*100);
                System.out.println("Max delayed tram :" + maxDelay);
        }

    }

    public static void initArrivingPassengersEvents(PriorityQueue<Event> eventQueue,List<Stop> stopList,LocalTime simulationStartTime){

        for (int i=0;i<stopList.size();i++){
            Event eventPassengerArrival = new Event(9,simulationStartTime,stopList.get(i));
            eventQueue.add(eventPassengerArrival);

        }
    }

    public static void initTramEvents(PriorityQueue<Event> eventQueue,int frequency,EndStop CSStop, EndStop PNRStop,LocalTime simulationStartTime){
        double PNR =  Math.ceil((17+TURN_AROUND_TIME_MINUTES)/(60/(double)frequency));
        double CS =  Math.floor((17+TURN_AROUND_TIME_MINUTES)/(60/(double)frequency));
        for(int i=0;i<CS;i++) {
            Tram tram = new Tram();
            tram.setNextStop(CSStop);
            tram.setCurrentStop(CSStop);
            tram.setTramNum(i + 1);
            LocalTime t =  LocalTime.of(simulationStartTime.getHour(),simulationStartTime.getMinute(),simulationStartTime.getSecond());
            t=t.plusMinutes((i*60/frequency) - 1);
            tram.setDepartureTime(t);
            tram.setPlannedArrivalTime(t);
            tram.setDirection(0);
            Event arriving = new Event(5, t, tram);
            CSStop.getaSwitch().addIncomming(tram);
            eventQueue.add(arriving);
        }

        for(int i=0;i<PNR;i++) {
            Tram tram = new Tram();
            tram.setCurrentStop(PNRStop);
            tram.setNextStop(PNRStop);
            tram.setTramNum(i+10);
            LocalTime t =  LocalTime.of(simulationStartTime.getHour(),simulationStartTime.getMinute(),simulationStartTime.getSecond());
            t=t.plusMinutes((i*60/frequency)-1 );
            tram.setDepartureTime(t);
            tram.setPlannedArrivalTime(t);
            tram.setDirection(0);
            Event arriving = new Event(5, t, tram);
            PNRStop.getaSwitch().addIncomming(tram);
            eventQueue.add(arriving);
        }

    }
    public static void initStops(    List<Stop> routeCSPNR,List<Stop> routePNRCS,List<Stop> stopList,EndStop CSStop,EndStop PNRStop){
        Stop stopB7 = new Stop( "Vaartsche Rijn", 0 ,17) ;
        Stop stopB6 = new Stop(  "Galgenwaard", 0,16 );
        Stop stopB5 = new Stop(  "De Kromme Rijn", 0 ,15);
        Stop stopB4 = new Stop( "Padualaan", 0 ,14) ;
        Stop stopB3 = new Stop(  "Heidelberglaan", 0 ,13);
        Stop stopB2 = new Stop( "UMC", 0 ,12) ;
        Stop stopB1 = new Stop(  "WKZ", 0 ,11);

        Stop stopA1 = new Stop( "Vaartsche Rijn", 0,2 ) ;
        Stop stopA2 = new Stop(  "Galgenwaard", 0 ,3);
        Stop stopA3 = new Stop(  "De Kromme Rijn", 0 ,4);
        Stop stopA4 = new Stop( "Padualaan", 0 ,5 ) ;
        Stop stopA5 = new Stop(  "Heidelberglaan", 0 ,6);
        Stop stopA6 = new Stop( "UMC", 0 ,7 ) ;
        Stop stopA7 = new Stop(  "WKZ", 0 ,8 );

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



    }
}
