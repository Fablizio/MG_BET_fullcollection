package it.smibet.repository;

import it.smibet.domain.PaymentsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsDataRepository extends JpaRepository<PaymentsData, Integer> {
}
