package com.nzt.ticketservice.services;

import com.google.common.collect.Maps;
import com.nzt.ticketservice.data.SeatHold;
import com.nzt.ticketservice.data.Seat;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class SeatReserveServiceImpl implements SeatReserveService {

    @Autowired
    private HoldService holdService;

    private final Map<String, String> confirmationNumbersMap = Maps.newHashMap();
    private final ConcurrentSkipListMap<String, List<Seat>> reservedSeats = new ConcurrentSkipListMap<>();

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {


        SeatHold hold = holdService.findById(seatHoldId).orElse(null);
        if (hold != null && !BooleanUtils.isTrue(hold.getHoldExpired())) {
            hold.stopTimer();
            List<Seat> heldSeats = hold.getHeldSeats();
            reservedSeats.put(customerEmail, heldSeats);


            String confirmationNumber = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
            int count = 0;
            while (confirmationNumbersMap.containsKey(confirmationNumber)) {
                confirmationNumber = RandomStringUtils.randomAlphabetic(8);

                if (count > 10) {
                    confirmationNumber = RandomStringUtils.randomAlphabetic(10);
                }

                count++;
            }
            confirmationNumbersMap.put(confirmationNumber, customerEmail);

            return confirmationNumber;
        } else if (hold == null) {
            throw new IllegalStateException("SeatHold could not be found by Id: " + String.valueOf(seatHoldId));
        }

        return StringUtils.EMPTY;

    }
}
