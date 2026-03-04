package com.betting.repository;

import com.betting.entity.Raddoppio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RaddoppioRepository extends JpaRepository<Raddoppio,Long> {

    boolean existsByPubblicataGreaterThanAndPubblicataLessThan(Date date0000, Date date2359);

    Optional<Raddoppio> findByPubblicataGreaterThanAndPubblicataLessThanAndTypeEquals(Date date0000, Date date2359,String type);

    List<Raddoppio> findAllByOrderByPubblicataDesc();

    List<Raddoppio> findByPubblicataGreaterThanEqualOrderByPubblicataDesc(Date date);
}
