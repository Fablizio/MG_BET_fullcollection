package com.betting.repository;

import com.betting.entity.Token;
import com.betting.entity.User;
import com.betting.enumeration.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    long countByUserAndStatus(User user, TokenStatus status);
    List<Token> findByUserAndStatus(User user, TokenStatus status);

}
