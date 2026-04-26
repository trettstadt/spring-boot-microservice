package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for booking entries.
 */
public interface BookingEntryRepository extends JpaRepository<BookingEntryEntity, BigInteger> {

}
