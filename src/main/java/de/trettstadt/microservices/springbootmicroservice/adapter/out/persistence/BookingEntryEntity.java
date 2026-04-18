package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity(name = "BookingEntry")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntryEntity {
    @Id
    private BigInteger id;

    private String description;
}
