package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.Booking;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        List<Booking> result = mapper.toPort(List.of(entity));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(BigInteger.ONE);
        assertThat(result.get(0).description()).isEqualTo("test");
    }

    @Test
    void shouldMapEmptyList() {
        // given
        List<BookingEntryEntity> input = Collections.emptyList();

        // when
        List<Booking> result = mapper.toPort(input);

        // then
        assertThat(result).isEmpty();
    }
}