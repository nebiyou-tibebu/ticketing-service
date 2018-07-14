package com.nzt.ticketservice.services;

import com.nzt.ticketservice.data.SeatHold;
import com.nzt.ticketservice.exceptions.NoMoreSeats;
import com.nzt.ticketservice.exceptions.NotEnoughSeats;
import com.nzt.ticketservice.exceptions.ReserveTimeOutException;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @see TicketService
 */
@Service
@XSlf4j
public class TicketServiceImpl implements TicketService {


    @Autowired
    private HoldService holdService;


    @Autowired
    private SeatReserveService seatReserveService;


    @Override
    public int numSeatsAvailable() {
        return holdService.numOfAvaiableSeats();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        try {
            SeatHold seatHold = holdService.holdSeats(numSeats, customerEmail);
            if (BooleanUtils.isTrue(seatHold.getSeatsAvailable())) {
                seatHold.startTimer();
            }
            return seatHold;

        } catch (NoMoreSeats noMoreSeats) {
            log.warn("There are not more seats available");
        } catch (NotEnoughSeats notEnoughSeats) {
           log.warn("Enough seats were not found");
        }
        return null;
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        try {
            String confirmationNumber = seatReserveService.reserveSeats(seatHoldId, customerEmail);
            return confirmationNumber;
        } catch (ReserveTimeOutException e) {
            log.warn("Hold time out for seat hold Id: {}, customer: {} ", seatHoldId, customerEmail);
        }
        return StringUtils.EMPTY;
    }

}
