package it.smibet.repository;

import it.smibet.domain.Constant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConstantRepository extends JpaRepository<Constant, String> {

    Optional<Constant> findByCode(String code);

}
