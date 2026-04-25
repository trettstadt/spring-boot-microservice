package de.trettstadt.microservices.springbootmicroservice.adapter.in.rest;

import de.trettstadt.microservices.springbootmicroservice.adapter.in.rest.api.model.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingApiMapper {

    List<Booking> toApi(List<de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking> bookings);
}
