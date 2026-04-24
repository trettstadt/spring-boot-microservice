package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import de.trettstadt.microservices.springbootmicroservice.application.port.out.Booking;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.FindBookings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingPersistenceAdapter implements FindBookings {
    private final BookingPersistenceMapper bookingPersistenceMapper;
    private final BookingEntryRepository bookingEntryRepository;

    @Override
    public List<Booking> findBookings() {
        return bookingPersistenceMapper.toPort(bookingEntryRepository.findAll());
    }
}
