package generators;

import entities.Passenger;
import entities.Stop;

import java.time.LocalTime;
import java.util.List;

public class PassengersArrivingGenerator {

    List<Passenger> generatePassengers(Stop stop){return null;}

    public PassengersArrivingGenerator() {
    }

    public int getNumPassengers(double mean, int time) {
        double L = Math.exp(-mean);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > L);

        return (k - 1)/getDenominator(time);
    }

    private int getDenominator(int time){
        if(time >= 60000 && time < 70000){
            return 4;
        } else if(time >= 70000 && time < 90000){
            return 8;
        } else if (time >= 90000 && time < 160000){
            return 28;
        } else if (time >= 160000 && time < 180000){
            return 8;
        } else if (time >= 180000){
            return 14;
        } else {
            return 1;
        }
    }

}
