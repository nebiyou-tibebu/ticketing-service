package com.nzt.ticketservice;


import com.nzt.ticketservice.services.TicketService;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
@XSlf4j
public class Cli implements ApplicationRunner {

    private final static String SERVER = "server";
    private final static String PORT = "port";

    @Value("${tcp.server.port}")
    private Integer defaultTcpServerPort;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private CommandLine cli;

    @Autowired
    private TcpServer server;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        boolean tcpServer = false;
        if (args.containsOption(SERVER)) {
            tcpServer = args.getOptionValues(SERVER).get(0).compareToIgnoreCase("y") == 0;
        }


        if (tcpServer) {

            // Get preferred port if provided
            Integer port = args.containsOption(PORT) ? Integer.valueOf(args.getOptionValues(PORT).get(0)) : defaultTcpServerPort;
            server.start(port);
            while(true) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
                String command = rd.readLine();
                if(StringUtils.equalsAnyIgnoreCase(command,"exit","quit")) {
                    SpringApplication.exit(ctx,() -> 0);
                    System.exit(0);
                }
            }

        } else {
            cli.start();
        }

    }
}
