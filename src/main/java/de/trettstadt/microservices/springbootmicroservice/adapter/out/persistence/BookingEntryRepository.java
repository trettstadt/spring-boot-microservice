package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

/**
 * Repository for booking entries.
 */
public interface BookingEntryRepository extends JpaRepository<BookingEntryEntity, BigInteger> {
}
