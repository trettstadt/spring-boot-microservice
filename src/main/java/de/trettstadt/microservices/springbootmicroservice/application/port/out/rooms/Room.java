package de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms;

import java.math.BigInteger;

public record Room(
        BigInteger id,
        String description
) {
}
