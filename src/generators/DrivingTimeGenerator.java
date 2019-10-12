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
}
