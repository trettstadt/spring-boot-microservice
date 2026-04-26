package de.trettstadt.microservices.springbootmicroservice.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.trettstadt.microservices.springbootmicroservice.adapter.in.rest.api.model.BookingList;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.BookingInPort;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.ListBookingsUseCase;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

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
    BookingInPort useCaseBooking = new BookingInPort(BigInteger.ONE, "test");
    de.trettstadt.microservices.springbootmicroservice.adapter.in.rest.api.model.Booking apiBooking =
        new de.trettstadt.microservices.springbootmicroservice.adapter.in.rest.api.model.Booking()
            .id(BigInteger.ONE)
            .description("test");

    when(listBookingsUseCase.getBookings()).thenReturn(List.of(useCaseBooking));
    when(bookingApiMapper.toApi(List.of(useCaseBooking))).thenReturn(List.of(apiBooking));

    // when
    ResponseEntity<BookingList> result = bookingController.getBookings();

    // then
    assertThat(result.getBody().getData()).extracting("id", "description")
        .containsExactly(tuple(BigInteger.ONE, "test"));
    verify(listBookingsUseCase).getBookings();
    verify(bookingApiMapper).toApi(List.of(useCaseBooking));
  }
}