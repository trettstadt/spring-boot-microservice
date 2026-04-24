package de.trettstadt.microservices.springbootmicroservice.adapter.in.rest;

import de.trettstadt.microservices.adapter.in.rest.BookingsApi;
import de.trettstadt.microservices.adapter.in.rest.model.BookingList;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.ListBookingsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BookingController implements BookingsApi {
    private final ListBookingsUseCase listBookingsUseCase;
    private final BookingApiMapper bookingApiMapper;

    public BookingController(ListBookingsUseCase listBookingsUseCase, BookingApiMapper bookingApiMapper) {
        this.listBookingsUseCase = listBookingsUseCase;
        this.bookingApiMapper = bookingApiMapper;
    }

    @Override
    @PreAuthorize("hasAuthority('SCOPE_bookings')")
    public ResponseEntity<BookingList> getBookings() {
        return ResponseEntity.of(Optional.of(new BookingList().data(bookingApiMapper.toApi(listBookingsUseCase.getBookings()))));
    }
}
