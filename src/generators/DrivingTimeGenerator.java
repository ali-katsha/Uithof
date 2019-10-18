package generators;

import entities.Stop;

import java.util.Random;

public  class DrivingTimeGenerator {


    public static long generateDrivingTime(Stop currentStop, Stop nextStop ){
        Random rand = new Random();
        int n = rand.nextInt(50);
        if (n>25)
        return 10;
        else
            return 10;
    }

    public static double generateDrivingTimeValidation(Stop currentStop, Stop nextStop, int valNumber){

        int[] avgDrivingTimes = {0, 134,243,59,101,60,86,78,113,110,0,110,78,82,60,100,59,243,135};

        Random r = new Random();
        double random = r.nextFloat();
        if (random < 0.4){
            return 0.8*avgDrivingTimes[currentStop.getStopNumber()];
        } else if (random >= 0.4 && random < 0.7){
            return avgDrivingTimes[currentStop.getStopNumber()];
        } else if (random >= 0.7 && random < 0.9){
            return avgDrivingTimes[currentStop.getStopNumber()] * 1.2;
        } else {
            return avgDrivingTimes[currentStop.getStopNumber()] * 1.4;
        }

    }


}
