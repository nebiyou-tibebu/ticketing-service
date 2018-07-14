package com.nzt.ticketservice;

import com.nzt.ticketservice.data.SeatHold;
import com.nzt.ticketservice.services.TicketService;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.net.Socket;
import java.util.regex.Pattern;

@XSlf4j
public class ClientThread extends Thread {

    private static final String REGEX_PATTERN = "\\S*@[A-za-z0-9]*.com$";


    private static final String PROMPT_1 = "Enter your email to reserve seats: (Type exit or quit to end session)";
    private static final String PROMPT_2 = "Enter the number of seats:";
    private static final String PROMPT_3 = "Reserve your seats? (Y/N)";

    private static final String REQUEST_EMAIL = "request-email";
    private static final String REQUEST_SEATS = "request-seats";
    private static final String CONFIRM_RESERVATION = "reserve";


    private final static String EXIT = "exit";
    private final static String QUIT = "quit";


    private Socket socket;

    private ApplicationContext ctx;


    public ClientThread(final Socket socket, final ApplicationContext ctx) {
        this.socket = socket;
        this.ctx = ctx;
    }

    @Override
    public void run() {

        try {

            final BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(new DataOutputStream(socket.getOutputStream())));

            toClient.write(REQUEST_EMAIL);
            toClient.flush();
            String response = fromClient.readLine();
            if (exit(response)) {
                close(fromClient, toClient);
                return;
            }

            if (!Pattern.matches(REGEX_PATTERN, response)) {
                toClient.write("Invalid email");
                toClient.flush();
                close(fromClient, toClient);
                return;
            } else {
                toClient.write(REQUEST_SEATS);
                toClient.flush();

                final String email = response;
                response = fromClient.readLine();
                if (!StringUtils.isNumeric(response)) {
                    toClient.write("Invalid request");
                    toClient.flush();
                    close(fromClient, toClient);
                    return;
                }

                final Integer requestedSeats = Integer.valueOf(response);

                TicketService service = ctx.getBean(TicketService.class);
                SeatHold hold = service.findAndHoldSeats(requestedSeats, email);
                if (hold != null && hold.getSeatsAvailable()) {
                    toClient.write(CONFIRM_RESERVATION);
                    toClient.flush();
                    response = fromClient.readLine();
                    if (StringUtils.equalsIgnoreCase(response, "Y")) {
                        if (!BooleanUtils.isTrue(hold.getHoldExpired())) {
                            toClient.write("Reservation confirmation: " + service.reserveSeats(hold.getSeatHoldId(), email));
                            toClient.flush();
                        }
                    }

                } else {
                    toClient.write("No more seats available");
                    toClient.flush();
                    close(fromClient, toClient);
                    return;
                }

            }

        } catch (IOException e) {
            log.error("Socket exception", e);
        }
    }

    private void close(final Reader reader, final Writer writer) throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }


    private boolean exit(String response) {
        return StringUtils.equalsAnyIgnoreCase(response, EXIT, QUIT);
    }
}
