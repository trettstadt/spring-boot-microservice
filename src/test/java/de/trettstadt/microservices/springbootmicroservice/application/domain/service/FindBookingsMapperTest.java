package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.domain.model.Booking;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class FindBookingsMapperTest {
    private final FindBookingsMapper mapper = new FindBookingsMapperImpl();

    @Test
    void shouldMapFromPort() {
        // given
        de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.Booking outBooking = new de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.Booking(BigInteger.ONE, "test");

        // when
        List<Booking> result = mapper.fromPort(List.of(outBooking));

        // then
        assertThat(result).extracting("id", "description")
                .containsExactly(tuple(BigInteger.ONE, "test"));
    }

    @Test
    void shouldMapEmptyList() {
        // given
        List<de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.Booking> input = Collections.emptyList();

        // when
        List<Booking> result = mapper.fromPort(input);

        // then
        assertThat(result).isEmpty();
    }
}