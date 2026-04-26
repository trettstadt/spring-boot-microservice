package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.trettstadt.microservices.springbootmicroservice.application.domain.model.BookingDomain;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.BookingOutPort;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class FindBookingsMapperTest {

  private final FindBookingsMapper mapper = new FindBookingsMapperImpl();

  @Test
  void shouldMapFromPort() {
    // given
    BookingOutPort outBooking = new BookingOutPort(
        BigInteger.ONE, "test");

    // when
    List<BookingDomain> result = mapper.fromPort(List.of(outBooking));

    // then
    assertThat(result).extracting("id", "description")
        .containsExactly(tuple(BigInteger.ONE, "test"));
  }

  @Test
  void shouldMapEmptyList() {
    // given
    List<BookingOutPort> input = Collections.emptyList();

    // when
    List<BookingDomain> result = mapper.fromPort(input);

    // then
    assertThat(result).isEmpty();
  }
}