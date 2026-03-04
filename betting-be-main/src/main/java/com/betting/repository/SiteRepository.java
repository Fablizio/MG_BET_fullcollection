package com.betting.repository;

import com.betting.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site,Long>{

    List<Site> findByActiveIsTrue();

    List<Site> findByIdGreaterThanEqualOrderByTerritorioAsc(Long id);

    boolean existsBySite(String site);

}
