package it.betting.batch.repository;

import it.betting.batch.entity.Raddoppio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RaddoppioRepository extends JpaRepository<Raddoppio,Long> {

    boolean existsByPubblicataGreaterThanAndPubblicataLessThanAndType(Date date0000, Date date2359,String type);

    List<Raddoppio> findByPubblicataLessThan(Date yesterday);
}
