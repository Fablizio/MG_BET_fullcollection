package com.betting.rest;

import com.betting.dto.FeedDTO;
import com.betting.dto.SiteFeedDTO;
import com.betting.service.IFeedService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CommonsLog
@RestController
@RequestMapping("/feedRSS")
public class RssRest {

    @Autowired
    IFeedService iFeedService;



    @GetMapping("/site")
    public ResponseEntity<List<SiteFeedDTO>> getSitesFeed(){
        return ResponseEntity.ok(iFeedService.getSiteFeedRSS());
    }

    @GetMapping("/{idSite}")
    public ResponseEntity<List<FeedDTO>> feedRss(@PathVariable("idSite")String idSite) {
        return ResponseEntity.ok(iFeedService.getNotizia(Long.parseLong(idSite)));
    }

}
