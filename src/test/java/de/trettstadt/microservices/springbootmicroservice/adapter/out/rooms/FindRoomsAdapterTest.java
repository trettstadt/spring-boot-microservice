package de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms;

import de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.RoomList;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindRoomsAdapterTest {
    @Mock
    private RoomsClient roomsClient;
    @Mock
    private RoomsMapper roomsMapper;

    private FindRoomsAdapter findRoomsAdapter;

    @BeforeEach
    void setUp() {
        findRoomsAdapter = new FindRoomsAdapter(roomsClient, roomsMapper);
    }

    @Test
    void shouldReturnRooms() {
        // given
        de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room externalRoom = new de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.Room();
        externalRoom.setId(BigInteger.ONE);
        externalRoom.setDescription("Test Room");

        RoomList roomList = new RoomList();
        roomList.setData(List.of(externalRoom));

        Room internalRoom = new Room(BigInteger.ONE, "Test Room");

        when(roomsClient.getRooms()).thenReturn(roomList);
        when(roomsMapper.toPort(List.of(externalRoom))).thenReturn(List.of(internalRoom));

        // when
        List<Room> result = findRoomsAdapter.findRooms();

        // then
        assertThat(result).containsExactly(internalRoom);
        verify(roomsClient).getRooms();
        verify(roomsMapper).toPort(List.of(externalRoom));
    }
}