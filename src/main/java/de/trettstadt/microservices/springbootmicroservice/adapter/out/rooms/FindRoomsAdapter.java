package de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.FindRooms;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.RoomOutPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Adapter implementing the {@link FindRooms} port for retrieving rooms from the external API.
 */
@Service
@RequiredArgsConstructor
public class FindRoomsAdapter implements FindRooms {

  private final RoomsClient roomsClient;
  private final RoomsMapper roomsMapper;

  @Override
  public List<RoomOutPort> findRooms() {
    return roomsMapper.toPort(roomsClient.getRooms().getData());
  }
}
