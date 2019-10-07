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

    public static int getNumPassengers(Stop stop, LocalTime time, String inOut) throws IOException {
        double mean = getStopMean(stop, inOut)[getTimePeriod2(time)];
        double p = 1.0;
        int k = 0;
        int step = 600;
        do {
            k++;
            p *= Math.random();
            while (p < 1 && mean > 0){
                if (mean > step){
                    p *= Math.exp(step);
                    mean -= step;
                } else {
                    p *= Math.exp(mean);
                    mean = 0;
                }
            }
        } while (p > 1);

        return (k - 1)/getDenominator(time);

    }

    private static double[] getStopMean(Stop stop, String inOut) throws IOException {
        double[][] means = new double[18][5];

        if (inOut.compareTo("in") == 0){
            // Stop number,time period
            means = getMeans("src/files/meanValuesIn.txt");
        } else if (inOut.compareTo("out") == 0){
            // Stop number,time period
            means = getMeans("src/files/meanValuesOut.txt");
        }


        if (stop.getStopNumber() == 1){
            return means[0];
        } else if (stop.getStopNumber() == 9){
            return means[9];
        } else {
            return means[stop.getStopNumber()-1];
        }

    }

    private static double[][] getMeans(String path) throws IOException {
        double[][] means = new double[18][5];
        int counter = 0;
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while((st = br.readLine()) != null){
            double [] tmp = new double[5];
            String[] tmpStr = st.split(",");
            for (int i = 0; i < tmpStr.length; i++){
                tmp[i] = Double.valueOf(tmpStr[i]);

            }

            means[counter] = tmp;
            counter++;
        }
        return means;
    }

    private static int getTimePeriod(int time){
        if(time >= 60000 && time < 70000){
            return 1;
        } else if(time >= 70000 && time < 90000){
            return 2;
        } else if (time >= 90000 && time < 160000){
            return 3;
        } else if (time >= 160000 && time < 180000){
            return 4;
        } else if (time >= 180000){
            return 5;
        } else {
            return 0;
        }
    }

    private static int getTimePeriod2(LocalTime time){
        if (time.isAfter(LocalTime.of(6,0,0)) && time.isBefore(LocalTime.of(7,0,1))){
            return 0;
        } else if (time.isAfter(LocalTime.of(7,0,0)) && time.isBefore(LocalTime.of(9,0,1))){
            return 1;
        } else if (time.isAfter(LocalTime.of(9,0,0)) && time.isBefore(LocalTime.of(16,0,1))){
            return 2;
        } else if (time.isAfter(LocalTime.of(16,0,0)) && time.isBefore(LocalTime.of(18,0,1))){
            return 3;
        } else if (time.isAfter(LocalTime.of(18,0,0))){
            return 4;
        } else {
            return 5;
        }

    }

    private static int getDenominator(LocalTime time){
        switch (getTimePeriod2(time)){
            case 0:
                return 4;
            case 1:
            case 3:
                return 8;
            case 2:
                return 28;
            case 4:
                return 14;
            default:
                return 1;
        }
    }
}