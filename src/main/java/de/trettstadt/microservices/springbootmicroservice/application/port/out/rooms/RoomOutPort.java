package de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms;

import java.math.BigInteger;

/**
 * Output port record representing a room in the external API.
 */
public record RoomOutPort(
    BigInteger id,
    String description
) {

}
