package de.trettstadt.microservices.springbootmicroservice.adapter.in.rest;

import de.trettstadt.microservices.adapter.in.rest.model.Booking;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(BigInteger.ONE);
        assertThat(result.get(0).getDescription()).isEqualTo("test");
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