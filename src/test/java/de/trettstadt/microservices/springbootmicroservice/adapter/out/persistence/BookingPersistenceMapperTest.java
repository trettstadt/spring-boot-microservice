package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.BookingOutPort;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class BookingPersistenceMapperTest {

  private final BookingPersistenceMapper mapper = new BookingPersistenceMapperImpl();

  @Test
  void shouldMapToPort() {
    // given
    BookingEntryEntity entity = BookingEntryEntity.builder()
        .id(BigInteger.ONE)
        .description("test")
        .build();

    // when
    List<BookingOutPort> result = mapper.toPort(List.of(entity));

    // then
    assertThat(result).extracting("id", "description")
        .containsExactly(tuple(BigInteger.ONE, "test"));
  }

  @Test
  void shouldMapEmptyList() {
    // given
    List<BookingEntryEntity> input = Collections.emptyList();

    // when
    List<BookingOutPort> result = mapper.toPort(input);

    // then
    assertThat(result).isEmpty();
  }
}