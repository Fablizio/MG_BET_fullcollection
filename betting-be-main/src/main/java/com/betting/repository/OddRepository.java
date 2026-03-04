package com.betting.repository;

import com.betting.dto.OddDTO;
import com.betting.entity.Odd;
import com.betting.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OddRepository extends JpaRepository<Odd, Long> {

    List<Odd> findByIdIn(List<Long> ids);

    Optional<Odd> findByTeamEquals(String team);

    Optional<List<Odd>> findBySiteEqualsAndPredictionIsNotNullAndFinalResultIsNotNullOrderByDateMatchDesc(Site site);

    Optional<List<Odd>> findBySiteEqualsAndPredictionIsNotNullAndDateMatchGreaterThanEqualOrderByDateMatchDesc(Site site, Date now);

    List<Odd> findByPredictionIsNotNullAndFinalResultIsNullAndDateMatchGreaterThanEqual(Date now);

    Optional<List<Odd>> findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNull(Date date0000, Date date2359);

    Optional<List<Odd>> findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNullAndStranaIsTrue(Date date0000, Date date2359);

    Optional<List<Odd>> findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNullAndStranaIsTrueAndFinalResultIsNotNull(Date date0000, Date date2359);

    Optional<List<Odd>> findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNullAndStranaIsFalseAndFinalResultIsNotNull(Date date0000, Date date2359);

    List<Odd> findByPredictionIsNotNullAndFinalResultIsNotNull();
}
