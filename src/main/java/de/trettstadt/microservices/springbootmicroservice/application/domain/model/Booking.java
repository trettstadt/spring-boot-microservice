package de.trettstadt.microservices.springbootmicroservice.application.domain.model;

import java.math.BigInteger;

public record Booking(
        BigInteger id,
        String description
) {
}
