package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.domain.model.BookingDomain;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.BookingInPort;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting between {@link BookingDomain} and {@link BookingInPort}.
 */
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ListBookingsUseCaseMapper {

  /**
   * Converts a domain booking to an input port.
   *
   * @param bookingDomain the domain booking
   * @return the input port
   */
  BookingInPort toPort(BookingDomain bookingDomain);

  /**
   * Converts a list of domain bookings to a list of input ports.
   *
   * @param bookingDomains the list of domain bookings
   * @return the list of input ports
   */
  List<BookingInPort> toPort(List<BookingDomain> bookingDomains);
}
