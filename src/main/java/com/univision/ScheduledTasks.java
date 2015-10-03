package com.univision;

import com.univision.properties.FeedsynProperties;
import com.univision.properties.XmlteamProperties;
import com.univision.validator.FeedValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private FeedsynProperties feedsyn;

    @Autowired
    private XmlteamProperties xmlteam;

    @Scheduled(fixedRate = 1000)
    public void reportCurrentTime() {
        LOGGER.info("Running validation at : " + dateFormat.format(new Date()));

        FeedValidator feedValidator = new FeedValidator();
        feedValidator.freshnessCheck(feedsyn.getUrl(), xmlteam.getManifest(), xmlteam.getBaseurl());
    }
}