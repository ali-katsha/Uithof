package generators;

import entities.Stop;
import entities.Tram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Random;

public class PassengersOutGenerator {

    public static int getNumPassengersValidation(Tram tram, Stop stop, LocalTime time, int valNumber) throws IOException {
                double prob = getStopMeanValidation(stop, time, valNumber);
        Random r = new Random();
        int counter = 0;
        for (int i = 0; i < tram.getPassengersNumber(); i++){
            double random = r.nextFloat();
            if (random < prob){
                counter++;
            }
        }
        return counter;
    }

    public static int getNumPassengers(Tram tram, Stop stop, LocalTime time) throws IOException {
        //return getNumPassengersValidation(tram,stop,time,3);

        double prob = getStopMean(stop, time);
        Random r = new Random();
        int counter = 0;
        for (int i = 0; i < tram.getPassengersNumber(); i++){
            double random = r.nextFloat();
            if (random < prob){
                counter++;
            }
        }
        return counter;
    }

    private static double getStopMean(Stop stop, LocalTime time) throws IOException {
        double[][] means;

        if (stop.getStopNumber() >= 1 && stop.getStopNumber() < 9 || stop.getStopNumber() == 18){
            means = getMeans("src/files/probOutRouteB.txt");
        } else {
            means = getMeans("src/files/probOutRouteA.txt");
        }


        if (stop.getStopNumber() == 1 || stop.getStopNumber() == 18){
            return means[getTimePeriod(time)][9];
        } else if (stop.getStopNumber() > 1 && stop.getStopNumber() <= 8){
            return means[getTimePeriod(time)][stop.getStopNumber()-1];
        } else if (stop.getStopNumber() == 9 || stop.getStopNumber() == 10){
            return means[getTimePeriod(time)][9];
        } else {
            return means[getTimePeriod(time)][stop.getStopNumber()%10];
        }

    }

    private static double getStopMeanValidation(Stop stop, LocalTime time, int valNumber) throws IOException {
        double[][] means;
        String fileName = "validation" + valNumber + "Out";

        if (stop.getStopNumber() >= 1 && stop.getStopNumber() < 9 || stop.getStopNumber() == 18){
            means = getMeans("src/files/validation/" + fileName + "B.txt");
        } else {
            means = getMeans("src/files/validation/" + fileName + "A.txt");
        }


        if (stop.getStopNumber() == 1 || stop.getStopNumber() == 18){
            return means[getTimePeriod(time)][9];
        } else if (stop.getStopNumber() > 1 && stop.getStopNumber() <= 8){
            return means[getTimePeriod(time)][stop.getStopNumber()-1];
        } else if (stop.getStopNumber() == 9 || stop.getStopNumber() == 10){
            return means[getTimePeriod(time)][9];
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
