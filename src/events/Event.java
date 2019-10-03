package events;

import java.time.LocalTime;

public class Event {

    int eventType;
    /* 1 : arrival at intermediate  stop , 2 : departure from intermediate stop, 3- Arriving at endpoint, 4-departure at end stop
    * 5- arrival at switch , 6- departure from switch , 7- Changing tracks (cross), 8- going straight at switch,
    * 9- passenger arrival
    * */
    LocalTime time;


    void eventHandler(){
        switch (eventType) {
            case 1:
                eventHandlerArriving();
        }
    }

    void eventHandlerArriving(){};
}
