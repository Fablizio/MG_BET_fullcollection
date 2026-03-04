package com.betting.repository;

import com.betting.entity.CodeFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendCodeRepository extends JpaRepository<CodeFriend,Long> {
}
