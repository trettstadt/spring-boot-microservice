package de.trettstadt.microservices.springbootmicroservice.adapter.in.rest;

import de.trettstadt.microservices.springbootmicroservice.adapter.in.rest.api.model.Booking;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class BookingApiMapperTest {
    private final BookingApiMapper mapper = new BookingApiMapperImpl();

    @Test
    void shouldMapToApi() {
        // given
        de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking useCaseBooking =
                new de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking(BigInteger.ONE, "test");

        // when
        List<Booking> result = mapper.toApi(List.of(useCaseBooking));

        // then
        assertThat(result).extracting("id", "description")
                .containsExactly(tuple(BigInteger.ONE, "test"));
    }

    @Test
    void shouldMapEmptyList() {
        // given
        List<de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking> input = Collections.emptyList();

        // when
        List<Booking> result = mapper.toApi(input);

        // then
        assertThat(result).isEmpty();
    }
}