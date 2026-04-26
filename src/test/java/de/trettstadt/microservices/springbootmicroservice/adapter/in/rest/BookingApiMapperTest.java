package de.trettstadt.microservices.springbootmicroservice.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.trettstadt.microservices.springbootmicroservice.adapter.in.rest.api.model.Booking;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.BookingInPort;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class BookingApiMapperTest {

  private final BookingApiMapper mapper = new BookingApiMapperImpl();

  @Test
  void shouldMapToApi() {
    // given
    BookingInPort useCaseBooking =
        new BookingInPort(
            BigInteger.ONE, "test");

    // when
    List<Booking> result = mapper.toApi(List.of(useCaseBooking));

    // then
    assertThat(result).extracting("id", "description")
        .containsExactly(tuple(BigInteger.ONE, "test"));
  }

  @Test
  void shouldMapEmptyList() {
    // given
    List<BookingInPort> input = Collections.emptyList();

    // when
    List<Booking> result = mapper.toApi(input);

    // then
    assertThat(result).isEmpty();
  }
}