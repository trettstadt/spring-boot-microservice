package de.trettstadt.microservices.springbootmicroservice.application.port.out.booking;

import java.util.List;

/**
 * Port interface for finding bookings in the persistence layer.
 */
public interface FindBookings {

  /**
   * Finds all bookings.
   *
   * @return list of booking output ports
   */
  List<BookingOutPort> findBookings();
}
