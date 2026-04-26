package de.trettstadt.microservices.springbootmicroservice.application.domain.model;

import java.math.BigInteger;

/**
 * Domain record representing a booking in the business layer.
 */
public record BookingDomain(
    BigInteger id,
    String description
) {

}
