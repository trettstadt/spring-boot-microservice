package de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.RoomOutPort;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoomsMapperTest {

  private final RoomsMapper mapper = new RoomsMapperImpl();

  @Test
  void shouldMapToPort() {
    // given
    de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room externalRoom = new de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room();
    externalRoom.setId(BigInteger.ONE);
    externalRoom.setDescription("Test Room");

    // when
    List<RoomOutPort> result = mapper.toPort(List.of(externalRoom));

    // then
    assertThat(result).extracting("id", "description")
        .containsExactly(tuple(BigInteger.ONE, "Test Room"));
  }

  @Test
  void shouldMapEmptyList() {
    // given
    List<de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room> input = Collections.emptyList();

    // when
    List<RoomOutPort> result = mapper.toPort(input);

    // then
    assertThat(result).isEmpty();
  }
}