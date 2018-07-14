package com.nzt.ticketservice;

import com.nzt.ticketservice.data.SeatHold;
import com.nzt.ticketservice.services.TicketService;
import com.nzt.ticketservice.data.Seat;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest()
@PropertySource("classpath:config.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TicketServiceApplicationTests {


    @Value("${hold.timeout.in.seconds}")
    private Integer holdTimeout;


    @Autowired
    private TicketService ticketService;

    @Test
    public void verifyA1_AvailableSeats() {
        int numSeatsAvailable = ticketService.numSeatsAvailable();
        Assert.assertEquals("Incorrect number of seats", 2600, numSeatsAvailable);
    }


    @Test
    public void verifyA2_SeatHold() {
        SeatHold seatHold = ticketService.findAndHoldSeats(400, "test@gmail.com");
        Assert.assertNotNull(seatHold);


        List<Seat> heldSeats = seatHold.getHeldSeats();
        Assert.assertNotNull(heldSeats);

        Assert.assertEquals("Incorrect number of held seats", 400, heldSeats.size());

        Assert.assertEquals("Incorrect number of remaining seats", 2200, ticketService.numSeatsAvailable());
    }

    @Test
    public void verifyA3_NotEnoughSeatsCaseIsCaught() {
        Assert.assertNull(ticketService.findAndHoldSeats(3000, "test@gmail.com"));
    }

    @Test
    public void verifyA4_ReserveSucceedsBeforeExpiration() {

        SeatHold seatHold = ticketService.findAndHoldSeats(4, "test@gmail.com");
        String confirmationNumber = ticketService.reserveSeats(seatHold.getSeatHoldId(), seatHold.getCustomerEmail());
        Assert.assertTrue(StringUtils.isNotBlank(confirmationNumber));

    }

    @Test
    public void verifyA5_ReserveFailsAfterTimeOut() {
        final SeatHold seatHold = ticketService.findAndHoldSeats(4, "test@gmail.com");
        try {
            Thread.sleep(holdTimeout * 1000 + 2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String confirmationNumber = ticketService.reserveSeats(seatHold.getSeatHoldId(), seatHold.getCustomerEmail());
        Assert.assertTrue("Invalid reservation", StringUtils.isBlank(confirmationNumber));
    }

    @Test
    public void verifyA6_NoMoreSeatsCaseIsCaught() {
        // Hold and Reserve all seats
        final SeatHold seatHold = ticketService.findAndHoldSeats(ticketService.numSeatsAvailable(), "test@gmail.com");
        String confirmationNumber = ticketService.reserveSeats(seatHold.getSeatHoldId(), seatHold.getCustomerEmail());

        Assert.assertTrue(StringUtils.isNotBlank(confirmationNumber));

        // Try and hold seats
        final SeatHold failCase = ticketService.findAndHoldSeats(1, "test@gmail.com");
        Assert.assertNull(failCase);

    }

}
