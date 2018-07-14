package com.nzt.ticketservice;

import com.nzt.ticketservice.data.SeatHold;
import com.nzt.ticketservice.services.TicketService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

@Service
public class CommandLine {

    private static final String REGEX_PATTERN = "\\S*@[A-za-z0-9]*.com$";


    private static final String PROMPT_1 = "Enter your email to reserve seats: (Type exit or quit to end session)";
    private static final String PROMPT_2 = "Enter the number of seats:";
    private static final String PROMPT_3 = "Reserve your seats? (Y/N)";

    private final static String EXIT = "exit";
    private final static String QUIT = "quit";


    @Autowired
    private TicketService ticketService;

    @Autowired
    private ApplicationContext ctx;

    public void start() throws IOException {

        BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println(new StringBuilder(PROMPT_1).append(StringUtils.SPACE).toString());
            String line = rd.readLine();
            if (StringUtils.isNotBlank(line) && !StringUtils.equalsIgnoreCase(line, EXIT)) {

                // Is the line an email
                if (!Pattern.matches(REGEX_PATTERN, line)) {
                    continue;
                }


                System.out.println(new StringBuilder(PROMPT_2).append(StringUtils.SPACE).toString());
                Integer numOfSeats = Integer.valueOf(rd.readLine());
                int availableSeats = ticketService.numSeatsAvailable();
                if (availableSeats >= numOfSeats) {
                    SeatHold hold = ticketService.findAndHoldSeats(numOfSeats, line);
                    System.out.println(PROMPT_3);
                    line = rd.readLine();
                    if (StringUtils.equalsIgnoreCase(line, "Y")) {
                        if (BooleanUtils.isNotTrue(hold.getHoldExpired())) {
                            System.out.println(new StringBuilder("Your reservation confirmation is: ").append(ticketService.reserveSeats(hold.getSeatHoldId(),
                                    hold.getCustomerEmail())).toString());
                            System.out.println();
                            System.out.println("=========================================================");
                            System.out.println();
                        } else {
                            System.out.println("Your hold has expired");
                            continue;
                        }
                    }
                } else if (availableSeats > 0) {
                    System.out.println(new StringBuilder("Only ").append(availableSeats).append(StringUtils.SPACE).append("seats available"));
                    continue;
                }
            } else if (StringUtils.equalsAnyIgnoreCase(line.trim(), EXIT, QUIT)) {
                SpringApplication.exit(ctx, () -> 0);
                System.exit(0);
            }
        }

    }
}
