package it.betting.batch.repository;

import it.betting.batch.entity.ReportDoubling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDoublingRepository extends JpaRepository<ReportDoubling,Long> {
}
