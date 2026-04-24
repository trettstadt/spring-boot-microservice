package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingPersistenceAdapterTest {
    @Mock
    private BookingPersistenceMapper bookingPersistenceMapper;
    @Mock
    private BookingEntryRepository bookingEntryRepository;

    private BookingPersistenceAdapter bookingPersistenceAdapter;

    @BeforeEach
    void setUp() {
        bookingPersistenceAdapter = new BookingPersistenceAdapter(bookingPersistenceMapper, bookingEntryRepository);
    }

    @Test
    void shouldReturnBookings() {
        // given
        BookingEntryEntity entity = BookingEntryEntity.builder()
                .id(BigInteger.ONE)
                .description("test")
                .build();
        Booking booking = new Booking(BigInteger.ONE, "test");

        when(bookingEntryRepository.findAll()).thenReturn(List.of(entity));
        when(bookingPersistenceMapper.toPort(List.of(entity))).thenReturn(List.of(booking));

        // when
        List<Booking> result = bookingPersistenceAdapter.findBookings();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(booking);
        verify(bookingEntryRepository).findAll();
        verify(bookingPersistenceMapper).toPort(List.of(entity));
    }
}