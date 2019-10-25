package generators;

import entities.Stop;

import java.util.Random;
import org.apache.commons.math3.distribution.LogNormalDistribution;


public  class DrivingTimeGenerator {

    public static long generateDrivingTime(Stop currentStop ){

        // return (long)generateDrivingTimeValidation(currentStop);
        return (long)generateRealDrivingTime(currentStop);
    }

    public static double generateRealDrivingTime(Stop currentStop){
        // Mean, Shape
        double data[][] = {{0,134,243,59,101,60,86,78,113,110,0,78,82,60,100,59,243,135}, //AvgDrivingTime
                {1,4.895792,5.491013,4.075489,4.613072,4.092296,4.452299,4.354661,4.72534,4.698432,1,4.354661,4.404671,4.092296,4.603122,4.075489,5.491013,4.903227}
        };
        LogNormalDistribution logNormal = new LogNormalDistribution(0.06400273,data[1][currentStop.getStopNumber()]);

        return data[0][currentStop.getStopNumber()] + Math.log(logNormal.sample());
    }

    public static double generateDrivingTimeValidation(Stop currentStop){

        int[] avgDrivingTimes = {0,134,243,59,101,60,86,78,113,110,0,78,82,60,100,59,243,135};

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
