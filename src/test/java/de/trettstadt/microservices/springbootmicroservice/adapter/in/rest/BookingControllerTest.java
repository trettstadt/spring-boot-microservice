package de.trettstadt.microservices.springbootmicroservice.adapter.in.rest;

import de.trettstadt.microservices.adapter.in.rest.model.BookingList;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.ListBookingsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private ListBookingsUseCase listBookingsUseCase;
    @Mock
    private BookingApiMapper bookingApiMapper;

    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        bookingController = new BookingController(listBookingsUseCase, bookingApiMapper);
    }

    @Test
    void shouldReturnBookingList() {
        // given
        Booking useCaseBooking = new Booking(BigInteger.ONE, "test");
        de.trettstadt.microservices.adapter.in.rest.model.Booking apiBooking =
                new de.trettstadt.microservices.adapter.in.rest.model.Booking()
                        .id(BigInteger.ONE)
                        .description("test");

        when(listBookingsUseCase.getBookings()).thenReturn(List.of(useCaseBooking));
        when(bookingApiMapper.toApi(List.of(useCaseBooking))).thenReturn(List.of(apiBooking));

        // when
        ResponseEntity<BookingList> result = bookingController.getBookings();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).hasSize(1);
        assertThat(result.getBody().getData().get(0).getDescription()).isEqualTo("test");
        verify(listBookingsUseCase).getBookings();
        verify(bookingApiMapper).toApi(List.of(useCaseBooking));
    }
}