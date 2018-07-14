package com.nzt.ticketservice.data;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class SeatHold extends Observable {

    private static int COUNTER = 1;

    private int holdTimeOut;

    @Getter
    private String customerEmail;

    @Getter
    private final List<Seat> heldSeats;
    private Timer holdTimer;

    @Getter
    private int seatHoldId;

    @Getter
    private Boolean holdExpired;

    @Getter
    @Setter
    private Boolean seatsAvailable = Boolean.FALSE;


    private synchronized static int updateCounter() {
        return COUNTER++;
    }

    public SeatHold() {
        this.seatHoldId = updateCounter();
        heldSeats = Lists.newLinkedList();
    }


    public SeatHold(int holdTimeOut, final String customerEmail, final List<Seat> heldSeats) {
        this.seatHoldId = updateCounter();
        this.holdTimeOut = holdTimeOut;
        this.heldSeats = heldSeats;
        this.customerEmail = customerEmail;
        holdTimer = new Timer();
    }

    public void startTimer() {
        holdTimer.schedule(new ReleaseHold(), holdTimeOut*1000);
    }

    public void stopTimer() {
        holdTimer.cancel();
    }

    private final class ReleaseHold extends TimerTask {

        @Override
        public void run() {
            setChanged();
            notifyObservers(heldSeats);
            holdExpired = true;
        }
    }

}
