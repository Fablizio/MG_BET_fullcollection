package com.betting.repository;

import com.betting.entity.Odd;
import com.betting.entity.QuotaAlert;
import com.betting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public interface QuotaAlertRepository extends JpaRepository<QuotaAlert, Long> {

    List<QuotaAlert> findByUserAndMatchIdIn(User user, List<Odd> odds);

}
