package de.trettstadt.microservices.springbootmicroservice.application.port.in;

import java.math.BigInteger;

/**
 * Input port record representing a booking in the application layer.
 */
public record BookingInPort(
    BigInteger id,
    String description
) {

}
