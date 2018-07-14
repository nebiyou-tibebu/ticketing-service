package com.nzt.ticketservice.exceptions;

public class NotEnoughSeats extends Exception {
    public NotEnoughSeats(String message) {
        super(message);
    }
}
