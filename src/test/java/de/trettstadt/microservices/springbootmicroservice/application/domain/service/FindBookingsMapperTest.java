package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.domain.model.Booking;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FindBookingsMapperTest {
    private final FindBookingsMapper mapper = new FindBookingsMapperImpl();

    @Test
    void shouldMapFromPort() {
        // given
        de.trettstadt.microservices.springbootmicroservice.application.port.out.Booking outBooking = new de.trettstadt.microservices.springbootmicroservice.application.port.out.Booking(BigInteger.ONE, "test");

        // when
        List<Booking> result = mapper.fromPort(List.of(outBooking));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(BigInteger.ONE);
        assertThat(result.get(0).description()).isEqualTo("test");
    }

    @Test
    void shouldMapEmptyList() {
        // given
        List<de.trettstadt.microservices.springbootmicroservice.application.port.out.Booking> input = Collections.emptyList();

        // when
        List<Booking> result = mapper.fromPort(input);

        // then
        assertThat(result).isEmpty();
    }
}