package de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.Room;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class RoomsMapperTest {
    private final RoomsMapper mapper = new RoomsMapperImpl();

    @Test
    void shouldMapToPort() {
        // given
        de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room externalRoom = new de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room();
        externalRoom.setId(BigInteger.ONE);
        externalRoom.setDescription("Test Room");

        // when
        List<Room> result = mapper.toPort(List.of(externalRoom));

        // then
        assertThat(result).extracting("id", "description")
                .containsExactly(tuple(BigInteger.ONE, "Test Room"));
    }

    @Test
    void shouldMapEmptyList() {
        // given
        List<de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room> input = Collections.emptyList();

        // when
        List<Room> result = mapper.toPort(input);

        // then
        assertThat(result).isEmpty();
    }
}