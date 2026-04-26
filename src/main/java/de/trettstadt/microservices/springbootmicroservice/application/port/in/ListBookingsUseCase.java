package de.trettstadt.microservices.springbootmicroservice.application.port.in;

import java.util.List;

/**
 * Use case interface for listing bookings.
 */
public interface ListBookingsUseCase {

  /**
   * Gets all bookings.
   *
   * @return list of booking input ports
   */
  List<BookingInPort> getBookings();
}
