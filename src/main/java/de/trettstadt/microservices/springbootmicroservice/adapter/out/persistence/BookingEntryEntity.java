package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a booking entry in the database.
 */
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
