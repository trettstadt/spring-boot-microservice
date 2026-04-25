package de.trettstadt.microservices.springbootmicroservice.application.port.out.booking;

import java.util.List;

public interface FindBookings {
    List<Booking> findBookings();
}
