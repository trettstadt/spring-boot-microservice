package de.trettstadt.microservices.springbootmicroservice.adapter.in.rest;

import de.trettstadt.microservices.springbootmicroservice.adapter.in.rest.api.model.Booking;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.BookingInPort;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting between API {@link Booking} and {@link BookingInPort}.
 */
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingApiMapper {

  /**
   * Converts a list of booking input ports to a list of API bookings.
   *
   * @param bookingInPorts the list of booking input ports
   * @return the list of API bookings
   */
  List<Booking> toApi(
      List<BookingInPort> bookingInPorts);
}
