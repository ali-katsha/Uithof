package generators;

import entities.EndStop;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeTableGenerator  {


    public static void generateTimeTable(EndStop endStop, LocalTime startTime, LocalTime endtime, int frequency, int offset )
    {
        List<LocalTime> plannedArrival = new ArrayList();
        List<LocalTime> plannedDeparture= new ArrayList();

        LocalTime time = startTime.plusMinutes(offset);

        while (time.isBefore(endtime))
        {
            plannedDeparture.add(time);
            plannedArrival.add(time.plusMinutes(22));
            time=time.plusMinutes(60/frequency);
        }

        endStop.setPlannedArrival(plannedArrival);
        endStop.setPlannedDeparture(plannedDeparture);
    }

}
