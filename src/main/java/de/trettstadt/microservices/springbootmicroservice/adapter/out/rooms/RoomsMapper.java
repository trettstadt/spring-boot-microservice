package de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.Room;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomsMapper {
    List<Room> toPort(List<de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room> rooms);
}
