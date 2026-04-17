package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "BookingEntry")
@Data
public class BookingEntryEntity {
    @Id
    private Long id;
}
