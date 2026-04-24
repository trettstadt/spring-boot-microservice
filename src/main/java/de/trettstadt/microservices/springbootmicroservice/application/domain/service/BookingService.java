package de.trettstadt.microservices.springbootmicroservice.application.domain.service;

import de.trettstadt.microservices.springbootmicroservice.application.port.in.Booking;
import de.trettstadt.microservices.springbootmicroservice.application.port.in.ListBookingsUseCase;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.FindBookings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements ListBookingsUseCase {
    private final FindBookings findBookings;
    private final FindBookingsMapper findBookingsMapper;
    private final ListBookingsUseCaseMapper listBookingsUseCaseMapper;

    @Override
    public List<Booking> getBookings() {
        return listBookingsUseCaseMapper.toPort(findBookingsMapper.fromPort(findBookings.findBookings()));
    }
}
