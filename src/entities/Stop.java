package entities;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class Stop {
    public Stop(String name, int direction,int stopNumber) {

        this.name = name;
        this.direction = direction;
        this.stopNumber = stopNumber;

        isBusy = false;
        passengerQueue = new Queue<Passenger>() {
            @Override
            public boolean add(Passenger passenger) {
                return false;
            }

            @Override
            public boolean offer(Passenger passenger) {
                return false;
            }

            @Override
            public Passenger remove() {
                return null;
            }

            @Override
            public Passenger poll() {
                return null;
            }

            @Override
            public Passenger element() {
                return null;
            }

            @Override
            public Passenger peek() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Passenger> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends Passenger> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        } ;

        maxWaitingTime=0;
        totalWaitingTime=0;
        numWaitPassenger=0;
    }


    public Stop(String name,int stopNumber) {

        this.name = name;
        this.direction = 0;
        this.stopNumber = stopNumber;

        isBusy = false;
        passengerQueue = new Queue<Passenger>() {
            @Override
            public boolean add(Passenger passenger) {
                return false;
            }

            @Override
            public boolean offer(Passenger passenger) {
                return false;
            }

            @Override
            public Passenger remove() {
                return null;
            }

            @Override
            public Passenger poll() {
                return null;
            }

            @Override
            public Passenger element() {
                return null;
            }

            @Override
            public Passenger peek() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Passenger> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends Passenger> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        } ;

        maxWaitingTime=0;
        totalWaitingTime=0;
        numWaitPassenger=0;
    }

    private String name;
    private boolean isBusy;
    private int direction; // 0 Station->pnr | 1 pnr-> station |
    Queue<Passenger> passengerQueue;
    private int stopNumber;


    long maxWaitingTime;
    long totalWaitingTime;
    long numWaitPassenger;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Queue<Passenger> getPassengerQueue() {
        return passengerQueue;
    }

    public void setPassengerQueue(Queue<Passenger> passengerQueue) {
        this.passengerQueue = passengerQueue;
    }

    public long getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public void setMaxWaitingTime(long maxWaitingTime) {
        this.maxWaitingTime = maxWaitingTime;
    }

    public long getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public void setTotalWaitingTime(long totalWaitingTime) {
        this.totalWaitingTime = totalWaitingTime;
    }

    public long getNumWaitPassenger() {
        return numWaitPassenger;
    }

    public void setNumWaitPassenger(long numWaitPassenger) {
        this.numWaitPassenger = numWaitPassenger;
    }


    public int getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(int stopNumber) {
        this.stopNumber = stopNumber;
    }
}
