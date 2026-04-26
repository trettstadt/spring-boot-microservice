package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.BookingOutPort;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting between {@link BookingEntryEntity} and {@link BookingOutPort}.
 */
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingPersistenceMapper {

  /**
   * Converts a list of booking entry entities to a list of booking output ports.
   *
   * @param bookingEntryEntities the list of booking entities
   * @return the list of booking output ports
   */
  List<BookingOutPort> toPort(List<BookingEntryEntity> bookingEntryEntities);
}
