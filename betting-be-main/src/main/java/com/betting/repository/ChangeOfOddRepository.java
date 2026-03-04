package com.betting.repository;

import com.betting.entity.ChangeOfOdd;
import com.betting.entity.Odd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeOfOddRepository extends JpaRepository<ChangeOfOdd,String> {

    List<ChangeOfOdd> findByOdd(Odd odd);

}
