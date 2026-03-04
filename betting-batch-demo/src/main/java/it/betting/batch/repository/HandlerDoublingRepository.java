package it.betting.batch.repository;

import it.betting.batch.entity.HandlerDoubling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HandlerDoublingRepository extends JpaRepository<HandlerDoubling,Long> {
}
