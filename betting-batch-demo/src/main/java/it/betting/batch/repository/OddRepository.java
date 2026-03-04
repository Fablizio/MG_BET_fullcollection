package it.betting.batch.repository;

import it.betting.batch.entity.Odd;
import it.betting.batch.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OddRepository extends JpaRepository<Odd,Long> {

    Optional<List<Odd>> findByPredictionIsNotNullAndPresaIsTrue();

    Optional<Odd> findByTeamEquals(String team);

    Optional<List<Odd>> findBySiteEqualsAndPredictionIsNotNullAndFinalResultIsNotNullOrderByDateMatchDesc(Site site);

    Optional<List<Odd>> findBySiteEqualsAndPredictionIsNotNullAndDateMatchGreaterThanEqualOrderByDateMatchDesc(Site site, Date now);

    List<Odd> findByPredictionIsNotNullAndFinalResultIsNullAndDateMatchGreaterThanEqual(Date now);

    Optional<List<Odd>> findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNull(Date date0000, Date date2359);
}

