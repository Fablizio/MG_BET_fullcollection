package it.betting.batch.html;

import it.betting.batch.dto.ResultOddDTO;
import it.betting.batch.entity.Odd;
import it.betting.batch.entity.Site;
import it.betting.batch.repository.SiteRepository;
import it.betting.batch.util.BettingUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@CommonsLog
public class Html {

    @Autowired
    BettingUtils utils;

    @Autowired
    SiteRepository siteRepository;


    public void updateSite() throws IOException {
        Document document = Jsoup.connect("https://www.betexplorer.com/").get();
        Elements lis = document.select("article").first().select("li");
        List<Site> sites = new ArrayList<>();
        lis.forEach(rs -> {

            try {

                if (rs.select("strong").first() != null) {

                    Elements liSubmenu = rs.select("li").get(0).select("li");

                    for (int i = 1; i < liSubmenu.size(); i++) {
                        Site site = new Site();
                        site.setTerritorio(rs.select("strong").first().text());
                        site.setCampionato(liSubmenu.get(i).text());
                        site.setSite("https://www.betexplorer.com" + liSubmenu.get(i).select("li").select("[href]").attr("href"));
                        sites.add(site);
                    }
                }
            } catch (Exception e) {
                log.error("Errore durante l'aggiornamento dei siti Exception --> ", e);
            }
        });


        List<Site> app = new ArrayList<>();

        sites.forEach(s->{
            if(!siteRepository.existsBySite(s.getSite()))
                app.add(s);
        });





        siteRepository.saveAll(app);
    }


    public List<ResultOddDTO> getFinalResult(Document document) {

        List<ResultOddDTO> list = new ArrayList<>();

        Elements tables = document.select("table.table-main");

        Element table = tables.get(0);

        if (tables.size() > 1) {
            table = tables.get(1);
        }

        for (Element row : table.select("tr")) {
            Elements td = row.select("td");

            if (td.size() == 0) continue;

            list.add(ResultOddDTO.builder()
                    .team(td.get(0).text())
                    .result(td.get(1).text())
                    .build()
            );

        }


        list.forEach(rs -> {
            if (rs.getResult().contains("PEN.")) {
                rs.setResult(rs.getResult().replace("PEN.", "").trim());
            }
            if (rs.getResult().contains("CAN.")) {
                rs.setResult(rs.getResult().replace("CAN.", "0:0").trim());
            }
            if (rs.getResult().contains("ET")) {
                rs.setResult(rs.getResult().replace("ET", "").trim());
            }
            if (rs.getResult().contains("AWA")) {
                rs.setResult(rs.getResult().replace("AWA.", "").trim());
            }
            if (rs.getResult().contains("ABN")) {
                rs.setResult(rs.getResult().replace("ABN.", "").trim());
            }
        });


        return list;
    }

    public List<Odd> parse(Document document) {

        boolean loadHomeTeamAndOuteHomeTeam;
        boolean load1;
        boolean loadX;
        boolean load2;
        boolean dataEventLoad;

        Element table = document.select("table.table-main").first();
        List<Odd> list = new ArrayList<>();
        int saltaPrimoGiro = 0;
        for (Element row : table.select("tr")) {

            if (saltaPrimoGiro == 0) {
                saltaPrimoGiro = 1;
                continue;
            }

            Elements td = row.select("td");

            Odd odd = new Odd();
            loadHomeTeamAndOuteHomeTeam = false;
            load1 = false;
            loadX = false;
            load2 = false;
            dataEventLoad = false;

            if (td.size() == 6) {
                continue;
            }

            for (Element e : td) {
                Elements eventFinisced = e.select("td[ title=\"Finished\"]");
                if (eventFinisced.size() != 0) {
                    odd = null;
                    break;
                }
                if (!loadHomeTeamAndOuteHomeTeam) {
                    Elements squadre = td.get(1).select("a[class=\"in-match\"]");
                    if (squadre.size() > 0) {
                        odd.setTeam(squadre.text());
                        loadHomeTeamAndOuteHomeTeam = true;
                    }
                }

                if (!load1) {
                    Elements uno = td.get(5).select("a[href]");
                    String attr = uno.attr("data-odd");
                    if (!"".equals(attr)) {
                        odd.setUno(Double.parseDouble(attr));
                        load1 = true;
                    }
                }
                if (!loadX) {
                    Elements x = td.get(6).select("a[href]");
                    String attr = x.attr("data-odd");
                    if (!"".equals(attr)) {
                        odd.setX(Double.parseDouble(attr));
                        loadX = true;
                    }

                }
                if (!load2) {
                    Elements due = td.get(7).select("a[href]");
                    String attr = due.attr("data-odd");
                    if (!"".equals(attr)) {
                        odd.setDue(Double.parseDouble(attr));
                        load2 = true;
                    }
                }
                if (!dataEventLoad) {
                    Element dataEvent = td.get(8);
                    if (!"".equals(dataEvent.text())) {
                        odd.setDataEvent(dataEvent.text());
                        dataEventLoad = true;
                    }
                }


            }
            if (odd != null)
                list.add(odd);
        }

        return list;
    }
}

