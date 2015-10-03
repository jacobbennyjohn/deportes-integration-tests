package com.univision.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbjohn.MapUtil;
import com.univision.feedsyn.FeedProcessor;
import com.univision.xmlteam.ManifestReader;
import com.univision.xmlteam.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 */
@Component
public class FeedValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedValidator.class);

    private Long notificationTtl = 30L;

    public void setNotificationTtl(Long notificationTtl) {
        this.notificationTtl = notificationTtl;
    }

    @SuppressWarnings("unchecked")
    @Async
	public void freshnessCheck(String fsynUrl, String manifestUrl, String feedDomain) {

        /**
         * 1. Check the manifest from xml team
         * 2. Fetch the files that are older than 30 seconds
         * 3. Remove duplicates
         * 4. Generate Feed syn url for the feeds objects
         * 5. Validate the feed against the feedsyn response
         */

        ManifestReader manifestReader = new ManifestReader();
        FeedProcessor fp = new FeedProcessor(fsynUrl);

        List<String> urlList = manifestReader.fetchLinksAndProcess(manifestUrl);
        int hashCode = urlList.hashCode();

        if (urlList != null) {
            LOGGER.info("Hashcode : " + hashCode + " => " + "Number of items found : " + urlList.size());
            for (String url : urlList) {
                try {
                    String originalFeed = manifestReader.getXMLTeamURL(feedDomain + url);
                    Normalizer normalizer = new Normalizer();
                    String response = normalizer.normalize(new ByteArrayInputStream(originalFeed.getBytes(StandardCharsets.UTF_8)));

                    HashMap<String, Object> jsonMap = new ObjectMapper().readValue(response, HashMap.class);

                    String date = (String) MapUtil.get(jsonMap, "$.sports-content.sports-metadata.@date-time");
                    String fixture = (String) MapUtil.get(jsonMap, "$.sports-content.sports-metadata.@fixture-key");
                    String key = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.event-metadata.@event-key");

                    LOGGER.info("Hashcode : " + hashCode + " => " + "Date/Fixture/Key : " + date + "/" + fixture + "/" + key);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                    Date documentDate = dateFormat.parse(date);

                    if (fixture != null && key != null) {
                        String feedResponse = fp.processFeed(fixture, key);
                        if (feedResponse != null) {
                            HashMap<String, Object> feedSynMap = new ObjectMapper().readValue(feedResponse, HashMap.class);
                            if (feedSynMap != null) {
                                String status = (String) MapUtil.get(feedSynMap, "$.status");
                                if (status.equals("success")) {
                                    String dateRecieved = (String) MapUtil.get(feedSynMap, "$.data.sports-content.sports-metadata.@date-time");
                                    LOGGER.info("Date/ReceivedDate : " + date + "/" + dateRecieved);
                                    if (!dateRecieved.equals(date)) {
                                        Date feedSynDocDate = dateFormat.parse(dateRecieved);
                                        Long lastUpdateDelay = TimeUnit.MILLISECONDS.toSeconds(documentDate.getTime() - feedSynDocDate.getTime());

                                        LOGGER.warn("Hashcode : " + hashCode + " => " + "Document not updated yet, date Actual/FeedSyn : " + date + "/" + dateRecieved + " == Fixture/Key : " + fixture + "/" + key);
                                        LOGGER.warn("Hashcode : " + hashCode + " => " + "Last update delay (in seconds) :" + lastUpdateDelay);

                                        if (lastUpdateDelay > notificationTtl) {
                                            LOGGER.error("Hashcode : " + hashCode + " => " + "Document not updated after " + lastUpdateDelay + " seconds, Fixture/Key : " + fixture + "/" + key);
                                        }
                                    }
                                } else {
                                    Date currentTime = new Date();
                                    Long lastUpdateDelay = TimeUnit.MILLISECONDS.toSeconds(documentDate.getTime() - currentTime.getTime());
                                    if (lastUpdateDelay > notificationTtl) {
                                        LOGGER.error("Hashcode : " + hashCode + " => " + "Document not available in FeedSyn after " + lastUpdateDelay + " seconds, Fixture/Key : " + fixture + "/" + key);
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error("IOException processing feeds", e);
                } catch (Exception e) {
                    LOGGER.error("Exception processing feeds", e);
                }
            }
            LOGGER.info("Hashcode : " + hashCode + " => " + "Processing complete!");
        }
    }
}
