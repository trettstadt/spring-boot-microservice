package de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms;

import java.util.List;

public interface FindRooms {
    List<Room> findRooms();
}
