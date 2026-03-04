package com.betting.repository;

import com.betting.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link,Long> {



}
