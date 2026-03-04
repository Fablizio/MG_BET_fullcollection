package com.betting.converter;

import com.betting.dto.*;
import com.betting.entity.*;
import com.betting.util.BettingUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Converter {

    public static SiteFeedDTO convertFromEntity(FeedRSSSite feedRSSSite) {
        return SiteFeedDTO.builder()
                .categoria(feedRSSSite.getCategoria())
                .id(feedRSSSite.getId())
                .build();
    }

    public static SiteDTO convertFromEntity(Site site) {


        return SiteDTO.builder()
                .id(site.getId())
                .campionato(site.getCampionato())
                .active(site.isActive())
                .url(site.getSite())
                .build();

    }

    public static OddDTO convertFromEntity(Odd odd, Set<QuotaAlert> filteredAlerts) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Date date = BettingUtils.parseDataEvent(odd.getDataEvent());
        Date dateMatch = odd.getDateMatch();

        if (date != null && dateMatch != null) {
            Calendar calDate = Calendar.getInstance();
            calDate.setTime(date);

            Calendar calMatch = Calendar.getInstance();
            calMatch.setTime(dateMatch);

            calMatch.set(Calendar.HOUR_OF_DAY, calDate.get(Calendar.HOUR_OF_DAY) + 1);
            calMatch.set(Calendar.MINUTE, calDate.get(Calendar.MINUTE));

            dateMatch = calMatch.getTime();
        }

        // null-safe per evitare NPE
        Set<QuotaAlert> safeAlerts =
                (filteredAlerts != null) ? filteredAlerts : new HashSet<>();

        return OddDTO.builder()
                .id(odd.getId() != null ? odd.getId().toString() : null)
                .dataAggiornamento(
                        odd.getAggiornamentoPrediction() != null
                                ? sdf.format(BettingUtils.add2Hours(odd.getAggiornamentoPrediction()))
                                : null
                )
                .team(odd.getTeam())
                .quotaInizialeUno(odd.getQuotaInizialeUno())
                .predictionConfidence(odd.getPredictionConfidence())
                .quotaInizialeX(odd.getQuotaInizialeX())
                .quotaInizialeDue(odd.getQuotaInizialeDue())
                .uno(odd.getUno())
                .due(odd.getDue())
                .predictionNote(odd.getPredictionNote())
                .x(odd.getX())
                .dataEvent(dateMatch != null ? sdf.format(dateMatch) : null)
                .result(odd.getFinalResult())
                .presa(odd.isPresa())
                .prediction(odd.getPrediction())
                .campionato(
                        odd.getSite() != null
                                ? odd.getSite().getTerritorio() + " - " + odd.getSite().getCampionato()
                                : null
                )
                .quotaAlerts(
                        safeAlerts.stream()
                                .map(quotaAlert -> OddDTO.QuotaAlertResponse.builder()
                                        .conditionType(quotaAlert.getConditionType())
                                        .quotaTarget(quotaAlert.getQuotaTarget())
                                        .quotaAlertId(quotaAlert.getId())
                                        .esito(quotaAlert.getEsito())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    public static OddDTO convertFromEntity(Odd odd) {
        return convertFromEntity(odd, odd.getQuotaAlerts());
    }




    public static SchedinaDTO createSchedina(OddDTO oddDTO) {

        SchedinaDTO schedina = convertFromDTO(oddDTO);

        if (ThreadLocalRandom.current().nextInt(1, 20) % 2 == 0) {
            schedina.setQuota(BettingUtils.calcolaQuotaDoppiaChange(oddDTO, schedina.getPrediction()));
            schedina.setPrediction(schedina.getPrediction().equals("1") ? "1X" : "X2");
        }

        return schedina;
    }

    public static SchedinaDTO convertFromDTO(OddDTO odd) {
        String prediction = odd.getPrediction().split("oppure")[0].trim();
        return SchedinaDTO.builder()
                .campionato(odd.getCampionato())
                .team(odd.getTeam())
                .prediction(prediction)
                .finalResult(odd.getResult())
                .quota(prediction.equals("1") ? odd.getUno() : odd.getDue())
                .dateMatch(odd.getDataEvent())
                .predictionNote(odd.getPredictionNote())
                .presa(odd.isPresa())
                .predictionConfidence(odd.getPredictionConfidence())
                .build();

    }

    public static RaddoppioDTO convertFromEntity(Raddoppio raddoppio) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        List<SchedinaDTO> result = raddoppio.getOdds()
                .stream()
                .filter(oddFilter -> oddFilter.getFinalResult() != null)
                .map(Converter::convertFromEntity)
                .peek(oddRaddoppio -> oddRaddoppio.setPresa(isPresa(oddRaddoppio.getResult(), oddRaddoppio.getPrediction())))
                .collect(Collectors.toList())
                .stream()
                .map(Converter::convertFromDTO)
                .collect(Collectors.toList());


        List<SchedinaDTO> list = result.stream().filter(SchedinaDTO::isPresa).collect(Collectors.toList());


        boolean preso = list.size() == 2;
        if (result.size() == 1) {
            preso = result.get(0).isPresa();
        }


        return RaddoppioDTO.builder()
                .id(raddoppio.getId())
                .dataRaddoppio(sdf.format(raddoppio.getPubblicata()))
                .odds(result)
                .preso(preso)
                .build();
    }

    private static boolean isPresa(String finalResult, String prediction) {

        String[] resultSplittato = finalResult.split(":");
        String predictionSplit = prediction.split("oppure")[0].trim();
        if (resultSplittato.length > 1) {

            int uno = Integer.parseInt(resultSplittato[0]);
            int due = Integer.parseInt(resultSplittato[1]);

            String finalPrediction = "";

            if (uno > due)
                finalPrediction = "1";
            if (due > uno)
                finalPrediction = "2";


            return finalPrediction.equals(predictionSplit);
        }

        return false;
    }

    public static LinkDTO convertFromEntity(Link link) {

        return LinkDTO.builder()
                .dataPubblicazione(BettingUtils.convertDateToString(link.getDataPubblicazione()))
                .facebook(link.getFacebook())
                .instagram(link.getInstagram())
                .present(true)
                .build();
    }

    public static UserDTO convertFromEntity(User user) {
        return UserDTO.builder()
                .code(user.getCode())
                .friendCodeActive(user.isFriendCodeActive())
                .subscription(user.getPayment() != null && user.getPayment() > 0)
                .friendCode(user.getFriendCode())
                .nickName(user.getNickname())
                .username(user.getUsername())
                .discordUsername(user.getDiscordUsername())
                .expireDate(BettingUtils.convertDateToString(user.getExpiration()))
                .build();
    }
}
