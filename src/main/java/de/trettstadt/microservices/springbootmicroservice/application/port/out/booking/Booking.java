package de.trettstadt.microservices.springbootmicroservice.application.port.out.booking;

import java.math.BigInteger;

public record Booking(
        BigInteger id,
        String description
) {
}
