package generators;

import entities.Passenger;
import entities.Stop;

import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.util.Arrays;

public class PassengersArrivingGenerator {

    List<Passenger> generatePassengers(Stop stop){return null;}

    public PassengersArrivingGenerator() {
    }

    public static int getNumPassengers(Stop stop, LocalTime time) throws IOException {

            double mean = getStopMean(stop, time);
            System.out.println(mean);
            double p = 1.0;
            int k = 0;
            int step = 600;
            do {
                k++;
                p *= Math.random();
                while (p < 1 && mean > 0) {
                    if (mean > step) {
                        p *= Math.exp(step);
                        mean -= step;
                    } else {
                        p *= Math.exp(mean);
                        mean = 0;
                    }
                }
            } while (p > 1);

            return k - 1;


    }


    private static double getStopMean(Stop stop, LocalTime time) throws IOException {
        double[][] means;


        if (stop.getStopNumber() >= 1 && stop.getStopNumber() < 9 || stop.getStopNumber() == 18){
            means = getMeans("src/files/meanValuesRouteB.txt");
        } else {
            means = getMeans("src/files/meanValuesRouteA.txt");
        }


        if (stop.getStopNumber() == 1 || stop.getStopNumber() == 18){
            return means[getTimePeriod(time)][0];
        } else if (stop.getStopNumber() > 1 && stop.getStopNumber() <= 8){
            return means[getTimePeriod(time)][stop.getStopNumber()-1];
        } else if (stop.getStopNumber() == 9 || stop.getStopNumber() == 10){
            return means[getTimePeriod(time)][0];
        } else {
            return means[getTimePeriod(time)][stop.getStopNumber()%10];
        }

    }

    private static double[][] getMeans(String path) throws IOException {
        double[][] means = new double[63][9];
        int counter = 0;
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while((st = br.readLine()) != null){
            double [] tmp = new double[9];
            String[] tmpStr = st.split(",");
            for (int i = 0; i < tmpStr.length; i++){
                tmp[i] = Double.valueOf(tmpStr[i]);

            }

            means[counter] = tmp;
            counter++;
        }
        return means;
    }

    private static int getTimePeriod(LocalTime time) throws IOException {
        int[][] times = new int[62][4];
        int counter = 0;

        File file = new File("src/files/timePeriods.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while((st = br.readLine()) != null){
            int [] tmp = new int[4];
            String[] tmpStr = st.split(",|:");
            for (int i = 0; i < tmpStr.length; i++){
                tmp[i] = Integer.valueOf(tmpStr[i]);

            }
            times[counter] = tmp;
            counter++;
        }

        for (int i = 0; i < times.length; i++){
            if (time.isBefore(LocalTime.of(times[i][0],times[i][1],times[i][2]))){
                return times[i][3];
            }
        }

        return 62;
    }


}