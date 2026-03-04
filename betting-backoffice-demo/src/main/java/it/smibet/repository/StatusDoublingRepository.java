package it.smibet.repository;

import it.smibet.domain.StatusDoubling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusDoublingRepository extends JpaRepository<StatusDoubling,Long> {
}
