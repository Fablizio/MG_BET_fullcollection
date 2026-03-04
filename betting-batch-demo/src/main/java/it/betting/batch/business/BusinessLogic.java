package it.betting.batch.business;

import ch.qos.logback.core.encoder.EchoEncoder;
import it.betting.batch.dto.ResultOddDTO;
import it.betting.batch.email.EmailService;
import it.betting.batch.email.StackTraceToString;
import it.betting.batch.entity.Odd;
import it.betting.batch.exception.BettingBatchException;
import it.betting.batch.html.Html;
import it.betting.batch.http.HttpClient;
import it.betting.batch.repository.SiteRepository;
import it.betting.batch.service.OddService;
import it.betting.batch.util.BettingUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
@CommonsLog
public class BusinessLogic {


    @Autowired
    HttpClient httpClient;

    @Autowired
    Html html;

    @Autowired
    OddService oddService;

    @Autowired
    BettingUtils utils;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    EmailService emailService;

    @Scheduled(cron = "0 */15 * ? * *")
    public void execute() {

        log.info("********************* START  ********************* ");

        AtomicReference<String> errore = new AtomicReference<>("Error ");

        try {


            siteRepository.findByActiveIsTrue().forEach(rs -> {

                try {
                    log.info("********************* SITE: " + rs.getSite());
                    Document htmlDocument = httpClient.getPageHtml(rs.getSite());
                    if (htmlDocument != null) {
                        List<ResultOddDTO> finalResult = html.getFinalResult(htmlDocument);
                        List<Odd> odds = html.parse(htmlDocument);
                        log.info("********************* START PREDICTION CALCULATE");
                        odds.forEach(oddFromSite -> {

                            try {
                                oddFromSite.setSite(rs);

                                if (!oddFromSite.isError()) {

                                    if (checkQuote(oddFromSite)) {
                                        Odd oddDb = oddService.findByHomeTeamAndOutHomeTeam(oddFromSite.getTeam());

                                        if (oddDb != null) {
                                            if (calcoloDifferenzaQuoteMaggioreDiAlmeno005cent(oddFromSite, oddDb)) {

                                                //Calcolo il nuero delle volte che cambiano le quote
                                                oddDb.setOci(calcoloOci(oddFromSite, oddDb, oddDb.getOci()));
                                                oddDb.setOldUno(oddDb.getUno());

                                                oddDb.setOldX(oddDb.getX());
                                                oddDb.setOldDue(oddDb.getDue());

                                                double aggioOld = oddDb.getAggio();
                                                double aggioNew = (100 / oddFromSite.getUno()) + (100 / oddFromSite.getX()) + (100 / oddFromSite.getDue());
                                                oddDb.setAggio(aggioNew);

                                                if (oddDb.getOci() > 0 && oddDb.getOci() < 600) {
                                                    double quotaPreferita = calcoloQuotaPiuBassa(oddFromSite);
                                                    String segnoPrediction = calcoloSegno(oddFromSite, quotaPreferita);
                                                    if (segnoPrediction.equals("1")) {
                                                        segnoPrediction = "1 oppure 1X";
                                                    } else
                                                        segnoPrediction = "2 oppure X2";


                                                    segnoPrediction = calcoloPartitaStrana(oddDb, oddFromSite, segnoPrediction);


                                                    oddDb.setPrediction(segnoPrediction);
                                                } else {
                                                    double quotaPiuAltaDB = calcoloQuotaPiuAlta(oddDb);
                                                    String segnoDB = calcoloSegno(oddDb, quotaPiuAltaDB);
                                                    double quotaPiuAltaSite = calcoloQuotaPiuAlta(oddFromSite);
                                                    String segnoSite = calcoloSegno(oddFromSite, quotaPiuAltaSite);

                                                    double quotaPiuBassaDB = calcoloQuotaPiuBassa(oddDb);
                                                    double quotaPiuBassaSite = calcoloQuotaPiuBassa(oddFromSite);


                                                    //AGGIO NUOVO SCENDE
                                                    if (aggioOld > 0 && aggioNew < aggioOld) {
                                                        if (segnoDB.equals(segnoSite) && quotaPiuAltaSite < quotaPiuAltaDB) {
                                                            double differezaQuote = quotaPiuAltaDB - quotaPiuAltaSite;
                                                            if (differezaQuote > 0.10) {
                                                                String segnoPrediction = calcoloSegno(oddFromSite, quotaPiuBassaSite);
                                                                if (segnoPrediction.equals("1")) {
                                                                    segnoPrediction = "1 oppure 1X";
                                                                } else
                                                                    segnoPrediction = "2 oppure X2";
                                                                oddDb.setPrediction(segnoPrediction);
                                                            }
                                                        } else
                                                            prediction(oddFromSite, oddDb, quotaPiuAltaDB, segnoDB, quotaPiuAltaSite, segnoSite, quotaPiuBassaDB, quotaPiuBassaSite);
                                                    } else if (aggioOld > 0 && aggioNew > aggioOld) {
                                                        if (segnoDB.equals(segnoSite) && quotaPiuAltaSite < quotaPiuAltaDB) {
                                                            String segnoPrediction = calcoloSegno(oddFromSite, quotaPiuAltaSite);
                                                            if (segnoPrediction.equals("1")) {
                                                                segnoPrediction = "1 oppure 1X";
                                                            } else
                                                                segnoPrediction = "2 oppure X2";
                                                            oddDb.setPrediction(segnoPrediction);
                                                        } else {
                                                            prediction(oddFromSite, oddDb, quotaPiuAltaDB, segnoDB, quotaPiuAltaSite, segnoSite, quotaPiuBassaDB, quotaPiuBassaSite);
                                                        }
                                                    }
                                                }

                                                oddDb.setUno(oddFromSite.getUno());
                                                oddDb.setX(oddFromSite.getX());
                                                oddDb.setDue(oddFromSite.getDue());
                                                oddDb.setAggiornamentoPrediction(utils.nowDate());
                                                oddDb.setDataEvent(oddFromSite.getDataEvent());
                                                oddService.save(oddDb);
                                            } else
                                                log.info("********************* Non ci sono stati cambiamenti di quota significativi la partita: " + oddDb.getTeam());

                                        } else {

                                            //Inserisco la partita solo se è presente la data dell'evento
                                            Date data = utils.getDate(oddFromSite.getDataEvent());
                                            if (data != null) {
                                                oddFromSite.setDateMatch(data);
                                                oddFromSite.setQuotaInizialeX(oddFromSite.getX());
                                                oddFromSite.setQuotaInizialeDue(oddFromSite.getDue());
                                                oddFromSite.setQuotaInizialeUno(oddFromSite.getUno());
                                                oddFromSite.setAggiornamentoPrediction(utils.nowDate());
                                                oddService.save(oddFromSite);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                errore.set("Site: " + rs.getSite() + "\n" +
                                        "Odds --> " + oddFromSite + " \n"
                                        + StackTraceToString.convert(e));
                                throw new BettingBatchException(StackTraceToString.convert(e));
                            }

                        });

                        log.info("********************* START AGGIORNAMENTO RISULTATI");
                        finalResult.forEach(resultOddDTO -> {

                            Odd entity = oddService.findByHomeTeamAndOutHomeTeam(resultOddDTO.getTeam());

                            if (entity != null) {
                                entity.setFinalResult(resultOddDTO.getResult());
                                oddService.update(entity);
                            }
                        });
                    }
                } catch (Exception e) {
                    errore.set("Site: " + rs.getSite() + "\n" +
                            StackTraceToString.convert(e));
                   // throw new BettingBatchException(StackTraceToString.convert(e));
                }

            });


            Date oraUndici = Date.from(LocalDateTime.now().withHour(9).withMinute(0).toInstant(ZoneOffset.UTC));


            if (new Date().after(oraUndici))
                oddService.createRaddoppio();



            if(errore.get() != null)
                throw new Exception();


        } catch (Exception e) {
            log.error("ERROREEEEE --> ".concat(errore.get()));
            //emailService.sendMail("Errore durante l'elaborazione Exception --> " + StackTraceToString.convert(e) + " \n ERRORE --> " + errore.get());
        }


        log.info("********************* END ********************* ");
    }

    private String calcoloPartitaStrana(Odd oddDb, Odd oddFromSite, String prediction) {

        double diffUno = oddDb.getQuotaInizialeUno() - oddFromSite.getUno();
        double diffDue = oddDb.getQuotaInizialeDue() - oddFromSite.getDue();

        // La quota uno si è alzata e la quota due si è abbassata in modo anomalo
        if (diffUno < 0 && Math.abs(diffUno) >= 0.10 && diffDue >= 0.40) {
            prediction = "2 oppure X2";
            oddDb.setStrana(true);
            log.info("******* PARTITA STRANA ******* -->" + oddFromSite.getTeam());

        }
        // La quota uno si è abbassata mentre la quota 2 si è alzata in modo anomalo
        if(diffUno > 0 && diffUno > 0.20  && diffDue < 0 && Math.abs(diffDue) >= 0.35){
            prediction = "1 oppure 1X";
            oddDb.setStrana(true);
            log.info("******* PARTITA STRANA ******* -->" + oddFromSite.getTeam());
        }

        return prediction;

    }


    private void prediction(Odd oddFromSite, Odd oddDb, double quotaPiuAltaDB, String segnoDB, double quotaPiuAltaSite, String segnoSite, double quotaPiuBassaDB, double quotaPiuBassaSite) {
        if (segnoDB.equals(segnoSite) && quotaPiuAltaSite > quotaPiuAltaDB && quotaPiuBassaSite < quotaPiuBassaDB) {

            String segnoPrediction = calcoloSegno(oddFromSite, quotaPiuBassaSite);
            if (segnoPrediction.equals("1")) {
                segnoPrediction = "1 oppure 1X";
            } else
                segnoPrediction = "2 oppure X2";
            oddDb.setPrediction(segnoPrediction);
        }
    }

    private boolean calcoloDifferenzaQuoteMaggioreDiAlmeno005cent(Odd oddFromSite, Odd oddDB) {

        return ((Math.abs(oddFromSite.getUno() - oddDB.getQuotaInizialeUno()) > 0.05)
                || (Math.abs(oddFromSite.getX() - oddDB.getQuotaInizialeX()) > 0.05)
                || (Math.abs(oddFromSite.getDue() - oddDB.getQuotaInizialeDue()) > 0.05));

    }

    private String calcoloSegno(Odd odd, double quota) {
        if (odd.getUno() == quota)
            return "1";

        if (odd.getX() == quota)
            return "X";

        if (odd.getDue() == quota)
            return "2";

        return "";
    }


    private double calcoloQuotaPiuBassa(Odd odd) {

        double[] vector = {odd.getUno(), odd.getX(), odd.getDue()};

        double minimo = vector[0];

        for (int i = 0; i <= vector.length - 1; i = i + 1) {
            if (vector[i] < minimo) {
                minimo = vector[i];
            }
        }

        return minimo;
    }

    private double calcoloQuotaPiuAlta(Odd odd) {

        double[] vector = {odd.getUno(), odd.getX(), odd.getDue()};

        double massino = vector[0];

        for (int i = 0; i <= vector.length - 1; i = i + 1) {
            if (vector[i] > massino) {
                massino = vector[i];
            }
        }

        return massino;
    }

    private boolean checkQuote(Odd odd) {
        return odd.getUno() != 0
                || odd.getX() != 0
                || odd.getDue() != 0;
    }


    private int calcoloOci(Odd oddFromSite, Odd oddDB, int change) {

        if ((oddFromSite.getUno() != oddDB.getUno())
                || (oddFromSite.getX() != oddDB.getX())
                || (oddFromSite.getDue() != oddDB.getDue())) {
            change = change + 1;
        }

        return change;
    }

    private double calcoloQuotaPreferita(Odd oddFromSite) {

        double quotaPreferita = oddFromSite.getDue();

        if (oddFromSite.getUno() < oddFromSite.getDue())
            quotaPreferita = oddFromSite.getUno();

        return quotaPreferita;

    }

}
