package com.betting.service.impl;

import com.betting.converter.Converter;
import com.betting.dto.FilterRequestDTO;
import com.betting.dto.LinkDTO;
import com.betting.dto.OddDTO;
import com.betting.dto.SchedinaDTO;
import com.betting.entity.Odd;
import com.betting.entity.QuotaAlert;
import com.betting.entity.Site;
import com.betting.entity.User;
import com.betting.http.HttpClient;
import com.betting.repository.*;
import com.betting.security.JwtAuthenticationFilter;
import com.betting.service.OddService;
import com.betting.util.BettingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.betting.security.JwtAuthenticationFilter.CODE;

@Service
public class OddServiceImpl implements OddService {

    @Autowired
    OddRepository oddRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    BettingUtils bettingUtils;

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private UserService userService;

    @Autowired
    private QuotaAlertRepository quotaAlertRepository;

    @Override
    public List<OddDTO> getOddsByTeam(String idTeam) {

        return getOddDTOS(idTeam);
    }

    @Override
    public List<OddDTO> getOddsBySite(String idSite) {
        return getOddDTOS(idSite);
    }

    private List<OddDTO> getOddDTOS(String idSite) {
        Optional<Site> siteOptional = siteRepository.findById(Long.valueOf(idSite));
        User user = userService.findByCode(httpServletRequest.getHeader(CODE));

        if (siteOptional.isPresent()) {
            Optional<List<Odd>> listaOdds = oddRepository.findBySiteEqualsAndPredictionIsNotNullAndDateMatchGreaterThanEqualOrderByDateMatchDesc(siteOptional.get(), bettingUtils.nowDate());
            if (listaOdds.isPresent()) {
                List<QuotaAlert> alerts = quotaAlertRepository.findByUserAndMatchIdIn(user, listaOdds.get());
                Map<Long, Set<QuotaAlert>> byMatch = alerts.stream()
                        .collect(Collectors.groupingBy(a -> a.getMatchId().getId(), Collectors.toSet()));


                return listaOdds.get().stream()
                        .map(o -> Converter.convertFromEntity(
                                o,
                                byMatch.getOrDefault(o.getId(), new HashSet<>())
                        ))
                        .collect(Collectors.toList());

            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<OddDTO> getHistoryByIdTeam(String idTeam) {

        Optional<Site> optionalSite = siteRepository.findById(Long.valueOf(idTeam));

        if (!optionalSite.isPresent())
            return new ArrayList<>();

        Optional<List<Odd>> optionalFinalResult = oddRepository.findBySiteEqualsAndPredictionIsNotNullAndFinalResultIsNotNullOrderByDateMatchDesc(optionalSite.get());

        return optionalFinalResult.map(odds -> odds
                .stream()
                .map(Converter::convertFromEntity)
                .collect(Collectors.toList())).orElseGet(ArrayList::new);

    }

    @Override
    public List<SchedinaDTO> generaSchedina() {

        final int MAX_EVENTI = 7;
        final int SOGLIA_CONFIDENCE = 70; // 0..100

        Date now = new Date();
        Date endOfDay = bettingUtils.nowDateWithHour23AndMinut59();

        // 1. Recupero dal DB (già filtrato prediction != null, finalResult == null, date >= now)
        List<Odd> listOdd = oddRepository
                .findByPredictionIsNotNullAndFinalResultIsNullAndDateMatchGreaterThanEqual(now);

        // 2. Filtro per oggi (o fino a endOfDay)
        List<Odd> todayOdds = listOdd.stream()
                .filter(o -> o.getDateMatch() != null)
                .filter(o -> !o.getDateMatch().before(now))
                .collect(Collectors.toList());

        // 3. Filtro sulla confidence (se presente)
        List<Odd> candidateOdds = todayOdds.stream()
                .filter(o -> {
                    Double conf = o.getPredictionConfidence();
                    return conf == null || conf >= SOGLIA_CONFIDENCE;
                })
                .collect(Collectors.toList());


        // Se ancora vuoto, ultimo fallback su todayOdds (niente di “intelligente” ma almeno qualcosa esce)
        if (candidateOdds.isEmpty()) {
            candidateOdds = todayOdds;
        }

        // 5. Ordinamento per "più sicura"
        candidateOdds.sort(
                Comparator
                        // prima la confidence (decrescente)
                        .comparing((Odd o) -> {
                            Double conf = o.getPredictionConfidence();
                            return conf != null ? conf : 0.0;
                        }).reversed()
                        // poi la quota 1 (crescente → più bassa = favoritissima)
                        .thenComparing(Odd::getUno)
                        // poi data del match
                        .thenComparing(Odd::getDateMatch)
        );

        // 6. Prendo i primi MAX_EVENTI senza duplicati
        int size = Math.min(MAX_EVENTI, candidateOdds.size());
        List<Odd> selected = candidateOdds.subList(0, size);

        // 7. Conversione in DTO e creazione schedina
        return selected.stream()
                .map(Converter::convertFromEntity)
                .map(Converter::createSchedina)
                .collect(Collectors.toList());
    }


    public static List<Odd> variazionePercentualeQuota1(List<Odd> oddsFromDB, int percentuale) {

        return oddsFromDB.stream()
                // prediction specifica (adatta se hai altri valori possibili)
                .filter(o -> "1 oppure 1X".equals(o.getPrediction()))
                // quota iniziale valida
                .filter(o -> o.getQuotaInizialeUno() > 0)
                // variazione percentuale
                .filter(o -> {
                    double quotaAttuale = o.getUno();
                    double quotaIniziale = o.getQuotaInizialeUno();

                    // variazione (quotaAttuale - quotaIniziale) / quotaIniziale * 100
                    double varPercentuale =
                            ((quotaAttuale - quotaIniziale) / quotaIniziale) * 100.0;

                    // se la quota è scesa di almeno "percentuale" %
                    return varPercentuale <= -percentuale;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<OddDTO> getTodayMatch() {
        return oddRepository
                .findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNull(bettingUtils.nowDateWithHour00AndMinut00(), bettingUtils.nowDateWithHour23AndMinut59())
                .map(odds -> odds
                        .stream()
                        .filter(rs -> !rs.isStrana())
                        .sorted(Comparator.comparing(Odd::getDateMatch))
                        .map(Converter::convertFromEntity)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    @Override
    public LinkDTO getLink() {

        AtomicReference<LinkDTO> linkDTO = new AtomicReference<>(new LinkDTO());

        linkRepository.findAll().forEach(rs -> linkDTO.set(Converter.convertFromEntity(rs)));

        return linkDTO.get();

    }

    @Override
    public List<OddDTO> getTodaySmellBet() {

        return oddRepository.findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNullAndStranaIsTrue(bettingUtils.nowDateWithHour00AndMinut00(), bettingUtils.nowDateWithHour23AndMinut59())
                .map(odds -> odds
                        .stream()
                        .sorted(Comparator.comparing(Odd::getDateMatch))
                        .map(Converter::convertFromEntity)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    @Override
    public List<OddDTO> getHistorySmellBet(String date) {
        return oddRepository.findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNullAndStranaIsTrueAndFinalResultIsNotNull(bettingUtils.nowDateWithHour00AndMinut00(date), bettingUtils.nowDateWithHour23AndMinut59(date))
                .map(odds -> odds
                        .stream()
                        .sorted(Comparator.comparing(Odd::getDateMatch))
                        .map(Converter::convertFromEntity)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);


    }

    @Override
    public List<OddDTO> getHistoryTodayMatch(String date) {
        return oddRepository.findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNullAndStranaIsFalseAndFinalResultIsNotNull(bettingUtils.nowDateWithHour00AndMinut00(date), bettingUtils.nowDateWithHour23AndMinut59(date))
                .map(odds -> odds
                        .stream()
                        .sorted(Comparator.comparing(Odd::getDateMatch))
                        .map(Converter::convertFromEntity)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);


    }

    @Override
    public List<OddDTO> getByFilter(FilterRequestDTO filterRequestDTO) {

        List<OddDTO> todayOdds;


        if (filterRequestDTO.isSmellBet()) {
            todayOdds = getTodaySmellBet();
        } else {
            todayOdds = getTodayMatch();
        }

        todayOdds.forEach(rs -> {
            rs.setPercentualeUno(((rs.getUno() - rs.getQuotaInizialeUno()) / rs.getUno()) * 100);
            rs.setPercentualeX(((rs.getX() - rs.getQuotaInizialeX()) / rs.getX()) * 100);
            rs.setPercentualeDue(((rs.getDue() - rs.getQuotaInizialeDue()) / rs.getDue()) * 100);
        });


        return todayOdds.stream().filter(rs -> filter(rs, filterRequestDTO)).collect(Collectors.toList());

    }

    @Override
    public List<OddDTO> findAll() {
        return oddRepository.findByPredictionIsNotNullAndFinalResultIsNotNull()
                .stream()
                .filter(rs -> rs.getFinalResult() != null && rs.getFinalResult().contains(":"))
                .map(Converter::convertFromEntity)
                .collect(Collectors.toList());
    }

    private boolean filter(OddDTO odd, FilterRequestDTO filterRequestDTO) {


        if ("UNO".equals(filterRequestDTO.getType())) {
            return odd.getPercentualeUno() <= filterRequestDTO.getPercentuale();
        }
        if ("X".equals(filterRequestDTO.getType())) {
            return odd.getPercentualeX() <= filterRequestDTO.getPercentuale();
        }
        if ("DUE".equals(filterRequestDTO.getType())) {
            return odd.getPercentualeDue() <= filterRequestDTO.getPercentuale();
        }

        return false;
    }

}
