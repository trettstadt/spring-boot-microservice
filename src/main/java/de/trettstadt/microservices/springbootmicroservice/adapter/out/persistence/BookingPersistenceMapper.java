package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingPersistenceMapper {
    List<Booking> toPort(List<BookingEntryEntity> bookingEntryEntities);
}
