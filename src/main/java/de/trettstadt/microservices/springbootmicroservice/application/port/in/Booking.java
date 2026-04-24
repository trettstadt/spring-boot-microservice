package de.trettstadt.microservices.springbootmicroservice.application.port.in;

import java.math.BigInteger;

public record Booking(
        BigInteger id,
        String description
) {
}
