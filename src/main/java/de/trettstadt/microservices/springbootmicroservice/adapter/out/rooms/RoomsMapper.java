package de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms;

import de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.RoomOutPort;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting between API {@link Room} and {@link RoomOutPort}.
 */
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomsMapper {

  /**
   * Converts a list of rooms to a list of room output ports.
   *
   * @param rooms the list of rooms from the API
   * @return the list of room output ports
   */
  List<RoomOutPort> toPort(List<Room> rooms);
}
