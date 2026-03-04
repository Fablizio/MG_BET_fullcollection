package it.betting.batch.repository;

import it.betting.batch.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site,Long> {

    List<Site> findByActiveIsTrue();

    boolean existsBySite(String site);
}
