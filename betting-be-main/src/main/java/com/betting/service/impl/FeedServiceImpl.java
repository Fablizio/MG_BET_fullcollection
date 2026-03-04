package com.betting.service.impl;

import com.betting.converter.Converter;
import com.betting.dto.FeedDTO;
import com.betting.dto.SiteFeedDTO;
import com.betting.repository.FeedRepository;
import com.betting.service.IFeedService;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CommonsLog
public class FeedServiceImpl implements IFeedService {

    @Autowired
    FeedRepository feedRepository;

    @Override
    public List<FeedDTO> getNotizia(long idSite) {

        List<FeedDTO> list = new ArrayList<>();

        feedRepository.findById(idSite)
                .ifPresent(site -> {

                    XmlReader reader;
                    try {
                        URL url = new URL(site.getSite());
                        reader = new XmlReader(url);
                        SyndFeed feed = new SyndFeedInput().build(reader);
                        for (Iterator i = feed.getEntries().iterator(); i.hasNext(); ) {
                            SyndEntry entry = (SyndEntry) i.next();

                            list.add(
                                    FeedDTO.builder()
                                            .autore(new String(entry.getAuthor().getBytes(), StandardCharsets.UTF_8))
                                            .link(entry.getLink())
                                            .titolo(new String(entry.getTitle().getBytes(StandardCharsets.UTF_8)))
                                            .descrizione(new String(entry.getDescription().getValue().getBytes(), StandardCharsets.UTF_8))
                                            .build()
                            );
                        }
                    } catch (Exception e) {
                        log.error("Errore durante la generazione dei feed rss Exception --> ", e);
                    }
                });


        return list;
    }

    @Override
    public List<SiteFeedDTO> getSiteFeedRSS() {
        return feedRepository.findByIdGreaterThanEqualOrderByCategoriaAsc(0L)
                .stream()
                .map(Converter::convertFromEntity)
                .collect(Collectors.toList());
    }
}
