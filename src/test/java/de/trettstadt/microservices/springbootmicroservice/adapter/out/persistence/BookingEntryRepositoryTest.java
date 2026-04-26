package de.trettstadt.microservices.springbootmicroservice.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest
@Tag("integration")
class BookingEntryRepositoryTest {

  private final BookingEntryRepository bookingEntryRepository;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Autowired
  BookingEntryRepositoryTest(BookingEntryRepository bookingEntryRepository) {
    this.bookingEntryRepository = bookingEntryRepository;
  }

  @Test
  void should_PerformCrudOperations() {
    // given (create)
    BookingEntryEntity givenEntity = BookingEntryEntity.builder().id(BigInteger.ONE)
        .description("test").build();

    // when (create)
    BookingEntryEntity actualCreated = transactionTemplate.execute(
        _ -> bookingEntryRepository.save(givenEntity));

    // then (create)
    assertThat(actualCreated).isNotNull();
    assertThat(actualCreated.getId()).isEqualTo(BigInteger.ONE);
    assertThat(actualCreated.getDescription()).isEqualTo("test");

    // given / when (read)
    Optional<BookingEntryEntity> actualRead = transactionTemplate.execute(
        _ -> bookingEntryRepository.findById(BigInteger.ONE));

    // then (read)
    assertThat(actualRead).isNotEmpty();
    assertThat(actualRead.get().getId()).isEqualTo(BigInteger.ONE);
    assertThat(actualRead.get().getDescription()).isEqualTo("test");

    // given (update)
    actualRead.get().setDescription("changed");

    // when (update)
    BookingEntryEntity actualUpdated = transactionTemplate.execute(
        _ -> bookingEntryRepository.save(actualRead.get()));

    // then (update)
    assertThat(actualUpdated).isNotNull();
    assertThat(actualUpdated.getId()).isEqualTo(BigInteger.ONE);
    assertThat(actualUpdated.getDescription()).isEqualTo("changed");

    // given / when (delete)
    transactionTemplate.executeWithoutResult(_ -> bookingEntryRepository.delete(actualUpdated));

    // then
    List<BookingEntryEntity> actualAfterDelete = transactionTemplate.execute(
        _ -> bookingEntryRepository.findAll());
    assertThat(actualAfterDelete).isEmpty();
  }
}