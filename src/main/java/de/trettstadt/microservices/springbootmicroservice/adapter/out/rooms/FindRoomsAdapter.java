package de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.FindRooms;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindRoomsAdapter implements FindRooms {
    private final RoomsClient roomsClient;
    private final RoomsMapper roomsMapper;

    @Override
    public List<Room> findRooms() {
        return roomsMapper.toPort(roomsClient.getRooms().getData());
    }
}
