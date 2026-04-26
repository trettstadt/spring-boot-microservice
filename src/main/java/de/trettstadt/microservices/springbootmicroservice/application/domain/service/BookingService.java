package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.port.in.BookingInPort;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.ListBookingsUseCase;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.FindBookings;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementing the {@link ListBookingsUseCase} for booking operations.
 */
@Service
@RequiredArgsConstructor
public class BookingService implements ListBookingsUseCase {

  private final FindBookings findBookings;
  private final FindBookingsMapper findBookingsMapper;
  private final ListBookingsUseCaseMapper listBookingsUseCaseMapper;

  @Override
  public List<BookingInPort> getBookings() {
    return listBookingsUseCaseMapper.toPort(
        findBookingsMapper.fromPort(findBookings.findBookings()));
  }
}
