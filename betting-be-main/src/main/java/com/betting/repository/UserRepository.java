package com.betting.repository;

import com.betting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByCode(String code);

    Optional<User>findByFriendCode(String friendCode);
}
