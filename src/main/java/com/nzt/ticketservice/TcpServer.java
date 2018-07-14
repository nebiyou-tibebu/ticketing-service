package com.nzt.ticketservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;

@Service
public class TcpServer {


    @Autowired
    private ApplicationContext ctx;

    private ServerSocket serverSocket;


    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        while (true) {
            try {
                new ClientThread(serverSocket.accept(), ctx).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
