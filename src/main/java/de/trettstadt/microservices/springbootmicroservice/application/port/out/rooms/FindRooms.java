package de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms;

import java.util.List;

/**
 * Port interface for finding rooms in the external API.
 */
public interface FindRooms {

  /**
   * Finds all rooms.
   *
   * @return list of room output ports
   */
  List<RoomOutPort> findRooms();
}
