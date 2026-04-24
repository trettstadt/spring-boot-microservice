package de.trettstadt.microservices.springbootmicroservice.application.port.out;

import java.math.BigInteger;

public record Booking(
        BigInteger id,
        String description
) {
}
