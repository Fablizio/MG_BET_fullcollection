package com.betting.repository;

import com.betting.entity.FeedRSSSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<FeedRSSSite,Long> {

    List<FeedRSSSite> findByIdGreaterThanEqualOrderByCategoriaAsc(Long id);

}
