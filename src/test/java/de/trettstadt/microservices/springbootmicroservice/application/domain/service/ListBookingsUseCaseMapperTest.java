package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.domain.model.Booking;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListBookingsUseCaseMapperTest {
    private final ListBookingsUseCaseMapper mapper = new ListBookingsUseCaseMapperImpl();

    @Test
    void shouldMapToPort() {
        // given
        Booking domainBooking = new Booking(BigInteger.ONE, "test");

        // when
        List<de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking> result = mapper.toPort(List.of(domainBooking));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(BigInteger.ONE);
        assertThat(result.get(0).description()).isEqualTo("test");
    }

    @Test
    void shouldMapEmptyList() {
        // given
        List<Booking> input = Collections.emptyList();

        // when
        List<de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking> result = mapper.toPort(input);

        // then
        assertThat(result).isEmpty();
    }
}