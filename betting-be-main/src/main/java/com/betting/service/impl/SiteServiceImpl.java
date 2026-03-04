package com.betting.service.impl;

import com.betting.converter.Converter;
import com.betting.dto.Site2DTO;
import com.betting.dto.SiteInsertRequest;
import com.betting.entity.Site;
import com.betting.html.Html;
import com.betting.repository.SiteRepository;
import com.betting.service.ISiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SiteServiceImpl implements ISiteService {

    @Autowired
    SiteRepository repository;

    @Autowired
    private Html html;

    @Override
    public List<Site2DTO> getSiteList() {
        List<Site> listSite = repository.findByIdGreaterThanEqualOrderByTerritorioAsc(0L);
        List<String> territori = listSite.stream().map(Site::getTerritorio).distinct().collect(Collectors.toList());

        List<Site2DTO> lista = new ArrayList<>();

        territori.forEach(territorio -> {
            Site2DTO ciccio = Site2DTO.builder()
                    .territorio(territorio)
                    .sites(
                            listSite.stream().filter(rs -> rs.getTerritorio().equals(territorio))
                                    .map(Converter::convertFromEntity)
                                    .collect(Collectors.toList())
                    )
                    .build();

            lista.add(ciccio);
        });


        return lista;

    }

    @Override
    public void updateSite() {
        html.updateSite();

    }

    @Override
    public void save(SiteInsertRequest request) {
        boolean exist = repository.existsBySite(request.getLink());

        if(exist) throw new RuntimeException("already-exist");

        repository.save(
            Site.builder()
                    .site(request.getLink())
                    .active(request.isActive())
                    .campionato(request.getCampionato())
                    .territorio(request.getTerritorio())
                    .build()
        );

    }

    @Override
    public void enabled(String siteId, SiteInsertRequest request) {
        repository.findById(Long.parseLong(siteId))
                .ifPresent(site -> {
                    site.setActive(request.isActive());
                    repository.save(site);
                });

    }
}
