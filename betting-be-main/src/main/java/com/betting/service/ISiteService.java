package com.betting.service;

import com.betting.dto.Site2DTO;
import com.betting.dto.SiteInsertRequest;

import java.util.List;

public interface ISiteService {
    List<Site2DTO> getSiteList();

    void updateSite() ;

    void save(SiteInsertRequest request);

    void enabled(String siteId, SiteInsertRequest request);
}
