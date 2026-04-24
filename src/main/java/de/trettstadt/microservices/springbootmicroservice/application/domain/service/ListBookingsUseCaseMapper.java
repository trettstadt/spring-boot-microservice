package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ListBookingsUseCaseMapper {
    Booking toPort(de.trettstadt.microservices.springbootmicroservice.application.domain.model.Booking booking);

    List<Booking> toPort(List<de.trettstadt.microservices.springbootmicroservice.application.domain.model.Booking> bookings);
}
