package com.betting.service;

import com.betting.dto.FeedDTO;
import com.betting.dto.SiteFeedDTO;

import java.util.List;

public interface IFeedService {

    List<FeedDTO> getNotizia(long idSite);

    List<SiteFeedDTO> getSiteFeedRSS();

}
