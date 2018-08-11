# Ticketing Service

A basic ticketing service application.

Assume a venue like so:

                   

            a1 ......... a100
            b1 ......... b100
                    .
                    .
                    .
            z1 ......... z100


## Features


The ticketing service agent accepts requests from a customer for a given number of seats and returns the best seats, if any are available.


## Design Choices

This is a simple command line application. It can also accept client connections via tcp. There is a client project ticketing-service-client.


## How Tos

1. Start the ticketing-service application using gradle's bootRun plugin. From the project directory enter command: gradle bootRun. Please note
that a version of Java 8 and Gradle must be in the Path.

2. If the application runs successfully, you will get the following prompts in sequence:

  a. To enter an email address
  b. To request X number of seats 
  c. To confirm a reservation

  If there seats available and reservation period has not expired, you will get an alphanumeric confirmation code.

## Scaling the solution

Two things would need to be done to scale this solution:

1. A shared resilient and replication repository with geographic considerations
2. A queuewhere incoming requests are queued and processed in turn.



