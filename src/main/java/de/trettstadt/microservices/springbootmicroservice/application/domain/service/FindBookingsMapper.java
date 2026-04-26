package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.domain.model.BookingDomain;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.BookingOutPort;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting between {@link BookingOutPort} and {@link BookingDomain}.
 */
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FindBookingsMapper {

  /**
   * Converts a booking output port to a domain booking.
   *
   * @param bookingOutPort the booking output port
   * @return the domain booking
   */
  BookingDomain fromPort(BookingOutPort bookingOutPort);

  /**
   * Converts a list of booking output ports to a list of domain bookings.
   *
   * @param bookingOutPorts the list of booking output ports
   * @return the list of domain bookings
   */
  List<BookingDomain> fromPort(List<BookingOutPort> bookingOutPorts);
}
