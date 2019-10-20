package generators;

import org.apache.commons.math3.distribution.GammaDistribution;

public class DwellTimeGenerator {

    public static double  generateDwellTime(int passIn, int passOut){
        double d = 12.5 + 0.22*passIn + 0.13*passOut;
        double dwellTime = 0.0;

        GammaDistribution dwellDist = new GammaDistribution(2,d/2);

        do {
            dwellTime = dwellDist.sample();
        } while (dwellTime < (d*0.8));

        return dwellTime;
    }
}
