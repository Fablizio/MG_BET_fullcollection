package com.betting.repository;

import com.betting.entity.AiElaboration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AiElaborationRepository extends JpaRepository<AiElaboration, Long> {
}
