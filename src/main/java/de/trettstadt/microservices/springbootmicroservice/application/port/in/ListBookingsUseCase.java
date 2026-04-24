package de.trettstadt.microservices.springbootmicroservice.application.port.in;

import java.util.List;

public interface ListBookingsUseCase {
    List<Booking> getBookings();
}
