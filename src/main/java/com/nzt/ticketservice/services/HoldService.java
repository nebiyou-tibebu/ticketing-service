package com.nzt.ticketservice.services;

import com.nzt.ticketservice.data.SeatHold;
import com.nzt.ticketservice.exceptions.NoMoreSeats;
import com.nzt.ticketservice.exceptions.NotEnoughSeats;

import java.util.Observer;
import java.util.Optional;

/**
 *  Manages holding and releasing seats for customers
 *
 *
 */
public interface HoldService extends Observer {

    /**
     *  Given the number of seats to hold and the customer email, method hold the requested number of seats if available
     *
     * @param numSeats - The number of seats
     * @param customerEmail - The customer email
     * @return - {@link SeatHold}
     */
    SeatHold holdSeats(int numSeats,String customerEmail) throws NoMoreSeats,NotEnoughSeats;

    /**
     * Find the seat hold by Id
     *
     * @param seatHoldId
     * @return SeatHold
     */
    Optional<SeatHold> findById(int seatHoldId);

    /**
     * Get Available seats
     *
     * @return - The number of seats that are available for hold.
     */
    int numOfAvaiableSeats();


}
