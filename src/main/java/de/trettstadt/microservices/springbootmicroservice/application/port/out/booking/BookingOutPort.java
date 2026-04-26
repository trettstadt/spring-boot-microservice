package de.trettstadt.microservices.springbootmicroservice.application.port.out.booking;

import java.math.BigInteger;

/**
 * Output port record representing a booking in the persistence layer.
 */
public record BookingOutPort(
    BigInteger id,
    String description
) {

}
