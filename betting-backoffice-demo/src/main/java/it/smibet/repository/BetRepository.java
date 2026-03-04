package it.smibet.repository;

import it.smibet.domain.Bet;
import it.smibet.enumeration.BetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet,Long> {

    List<Bet> findByBetType(BetType betType);

}
