package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.trettstadt.microservices.springbootmicroservice.application.domain.model.BookingDomain;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.BookingInPort;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListBookingsUseCaseMapperTest {

  private final ListBookingsUseCaseMapper mapper = new ListBookingsUseCaseMapperImpl();

  @Test
  void shouldMapToPort() {
    // given
    BookingDomain domainBooking = new BookingDomain(BigInteger.ONE, "test");

    // when
    List<BookingInPort> result = mapper.toPort(
        List.of(domainBooking));

    // then
    assertThat(result).extracting("id", "description")
        .containsExactly(tuple(BigInteger.ONE, "test"));
  }

  @Test
  void shouldMapEmptyList() {
    // given
    List<BookingDomain> input = Collections.emptyList();

    // when
    List<BookingInPort> result = mapper.toPort(
        input);

    // then
    assertThat(result).isEmpty();
  }
}