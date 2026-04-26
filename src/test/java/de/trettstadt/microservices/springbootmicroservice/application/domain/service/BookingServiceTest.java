package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.trettstadt.microservices.springbootmicroservice.application.domain.model.BookingDomain;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.BookingInPort;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.BookingOutPort;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.FindBookings;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock
  private FindBookings findBookings;
  @Mock
  private FindBookingsMapper findBookingsMapper;
  @Mock
  private ListBookingsUseCaseMapper listBookingsUseCaseMapper;

  private BookingService bookingService;

  @BeforeEach
  void setUp() {
    bookingService = new BookingService(findBookings, findBookingsMapper,
        listBookingsUseCaseMapper);
  }

  @Test
  void shouldReturnBookings() {
    // given
    BookingOutPort outBooking = new BookingOutPort(
        BigInteger.ONE, "test booking");
    BookingDomain domainBooking = new BookingDomain(BigInteger.ONE, "test booking");
    BookingInPort useCaseBooking = new BookingInPort(
        BigInteger.ONE, "test booking");

    when(findBookings.findBookings()).thenReturn(List.of(outBooking));
    when(findBookingsMapper.fromPort(List.of(outBooking))).thenReturn(List.of(domainBooking));
    when(listBookingsUseCaseMapper.toPort(List.of(domainBooking))).thenReturn(
        List.of(useCaseBooking));

    // when
    List<BookingInPort> result = bookingService.getBookings();

    // then
    assertThat(result).containsExactly(useCaseBooking);
    verify(findBookings).findBookings();
    verify(findBookingsMapper).fromPort(List.of(outBooking));
    verify(listBookingsUseCaseMapper).toPort(List.of(domainBooking));
  }
}