package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.BookingOutPort;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.FindBookings;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Persistence adapter implementing the {@link FindBookings} port for retrieving
 * bookings from the database.
 */
@Service
@RequiredArgsConstructor
public class BookingPersistenceAdapter implements FindBookings {

  private final BookingPersistenceMapper bookingPersistenceMapper;
  private final BookingEntryRepository bookingEntryRepository;

  @Override
  public List<BookingOutPort> findBookings() {
    return bookingPersistenceMapper.toPort(bookingEntryRepository.findAll());
  }
}
