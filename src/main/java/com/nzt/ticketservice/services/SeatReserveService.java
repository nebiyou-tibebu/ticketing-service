package com.nzt.ticketservice.services;

import com.nzt.ticketservice.exceptions.ReserveTimeOutException;

public interface SeatReserveService {

    /**
     * Commit seats held for a specific customer
     *
     * @param seatHoldId    the seat hold identifier
     * @param customerEmail the email address of the customer to which the
     *                      seat hold is assigned
     * @return a reservation confirmation code
     */
    String reserveSeats(int seatHoldId, String customerEmail) throws ReserveTimeOutException;


}
