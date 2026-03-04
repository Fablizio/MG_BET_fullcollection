package com.betting.repository;

import com.betting.entity.Odd;
import com.betting.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Odd, Long> {


    @Query(value = "select count(*) from odd where prediction is not null and date_match < :today and presa = 1 and final_result is not null",
            nativeQuery = true)
    int getTotalWin(@Param("today") Date today);

    @Query(value = "select count(*) from odd where prediction is not null and date_match < :today and presa = 0 and final_result is not null and final_result like '%:%'",
            nativeQuery = true)
    int getTotalLose(@Param("today") Date today);

    @Query(value = "select distinct DATE_FORMAT(date_match ,'%Y-%m-%d') from odd where prediction is not null and date_match < DATE_FORMAT(:now,'%Y-%m-%d') and site_id = :site ",
            nativeQuery = true)
    List<String> findByAllDateDistinct(@Param("now") Date now,@Param("site") long site);


    List<Odd> findByDateMatchGreaterThanEqualAndDateMatchLessThanEqualAndSiteAndPredictionIsNotNull(Date startDate, Date endDate, Site site);


    @Query(value = "select count(*) from odd where prediction is not null and date_match < :today and site_id = :site and presa = 1",
    nativeQuery = true)
    int getTotalWinPieChartStatisticsByCampionato(@Param("today") Date today,@Param("site") Long site);

    @Query(value = "select count(*) from odd where prediction is not null and date_match < :today and site_id = :site and presa = 0 and final_result is not null and final_result like '%:%'",
    nativeQuery = true)
    int getTotalLosePieChartStatisticsByCampionato(@Param("today") Date today,@Param("site") Long site);

}
