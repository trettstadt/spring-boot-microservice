package de.trettstadt.microservices.springbootmicroservice.adapter.in.rest;

import de.trettstadt.microservices.springbootmicroservice.adapter.in.rest.api.BookingsApi;
import de.trettstadt.microservices.springbootmicroservice.adapter.in.rest.api.model.BookingList;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.ListBookingsUseCase;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller implementing the {@link BookingsApi} for handling booking-related HTTP requests.
 */
@RestController
public class BookingController implements BookingsApi {

  private final ListBookingsUseCase listBookingsUseCase;
  private final BookingApiMapper bookingApiMapper;

  /**
   * Creates a new BookingController with the specified use case and mapper.
   *
   * @param listBookingsUseCase the use case for listing bookings
   * @param bookingApiMapper the mapper for converting between API and domain models
   */
  public BookingController(ListBookingsUseCase listBookingsUseCase,
      BookingApiMapper bookingApiMapper) {
    this.listBookingsUseCase = listBookingsUseCase;
    this.bookingApiMapper = bookingApiMapper;
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_bookings')")
  public ResponseEntity<BookingList> getBookings() {
    return ResponseEntity.of(Optional.of(
        new BookingList().data(bookingApiMapper.toApi(listBookingsUseCase.getBookings()))));
  }
}
