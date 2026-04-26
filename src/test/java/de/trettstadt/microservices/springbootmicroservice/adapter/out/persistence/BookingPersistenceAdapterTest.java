package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.BookingOutPort;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingPersistenceAdapterTest {

  @Mock
  private BookingPersistenceMapper bookingPersistenceMapper;
  @Mock
  private BookingEntryRepository bookingEntryRepository;

  private BookingPersistenceAdapter bookingPersistenceAdapter;

  @BeforeEach
  void setUp() {
    bookingPersistenceAdapter = new BookingPersistenceAdapter(bookingPersistenceMapper,
        bookingEntryRepository);
  }

  @Test
  void shouldReturnBookings() {
    // given
    BookingEntryEntity entity = BookingEntryEntity.builder()
        .id(BigInteger.ONE)
        .description("test")
        .build();
    BookingOutPort bookingOutPort = new BookingOutPort(BigInteger.ONE, "test");

    when(bookingEntryRepository.findAll()).thenReturn(List.of(entity));
    when(bookingPersistenceMapper.toPort(List.of(entity))).thenReturn(List.of(bookingOutPort));

    // when
    List<BookingOutPort> result = bookingPersistenceAdapter.findBookings();

    // then
    assertThat(result).containsExactly(bookingOutPort);
    verify(bookingEntryRepository).findAll();
    verify(bookingPersistenceMapper).toPort(List.of(entity));
  }
}