package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FindBookingsMapper {
    de.trettstadt.microservices.springbootmicroservice.application.domain.model.Booking fromPort(Booking booking);

    List<de.trettstadt.microservices.springbootmicroservice.application.domain.model.Booking> fromPort(List<Booking> bookings);
}
