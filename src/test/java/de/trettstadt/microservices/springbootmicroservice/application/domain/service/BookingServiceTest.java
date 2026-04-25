package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.domain.model.Booking;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.FindBookings;
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
        bookingService = new BookingService(findBookings, findBookingsMapper, listBookingsUseCaseMapper);
    }

    @Test
    void shouldReturnBookings() {
        // given
        de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.Booking outBooking = new de.trettstadt.microservices.springbootmicroservice.application.port.out.booking.Booking(BigInteger.ONE, "test booking");
        Booking domainBooking = new Booking(BigInteger.ONE, "test booking");
        de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking useCaseBooking = new de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking(BigInteger.ONE, "test booking");

        when(findBookings.findBookings()).thenReturn(List.of(outBooking));
        when(findBookingsMapper.fromPort(List.of(outBooking))).thenReturn(List.of(domainBooking));
        when(listBookingsUseCaseMapper.toPort(List.of(domainBooking))).thenReturn(List.of(useCaseBooking));

        // when
        List<de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking> result = bookingService.getBookings();

        // then
        assertThat(result).containsExactly(useCaseBooking);
        verify(findBookings).findBookings();
        verify(findBookingsMapper).fromPort(List.of(outBooking));
        verify(listBookingsUseCaseMapper).toPort(List.of(domainBooking));
    }
}