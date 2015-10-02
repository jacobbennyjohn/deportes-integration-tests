package com.univision.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbjohn.MapUtil;
import com.univision.feedsyn.FeedProcessor;
import com.univision.xmlteam.ManifestReader;
import com.univision.xmlteam.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class FeedValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedValidator.class);

    public static void freshnessCheck() {
        /**
         * 1. Check the manifest from xml team
         * 2. Fetch the files that are older than 30 seconds
         * 3. Remove duplicates
         * 4. Generate Feed syn url for the feeds objects
         * 5. Validate the feed against the feedsyn response
         */
        String manifestUrl = "http://feed5.xmlteam.com/api/feeds?start=PT2M&format=xml&sport-keys=15054000";
        String feedDomain = "http://feed5.xmlteam.com/sportsml/files/";
        ManifestReader manifestReader = new ManifestReader();
        FeedProcessor fp = new FeedProcessor();
        List<String> urlList = manifestReader.fetchLinksAndProcess(manifestUrl);
        if (urlList != null) {
            for (String url : urlList) {
                try {
                    String originalFeed = manifestReader.getXMLTeamURL(feedDomain + url);
                    Normalizer normalizer = new Normalizer();
                    String response = normalizer.normalize(new ByteArrayInputStream(originalFeed.getBytes(StandardCharsets.UTF_8)));

                    HashMap<String, Object> jsonMap = new ObjectMapper().readValue(response, HashMap.class);

                    String date = (String) MapUtil.get(jsonMap, "$.sports-content.sports-metadata.@date-time");
                    String fixture = (String) MapUtil.get(jsonMap, "$.sports-content.sports-metadata.@fixture-key");
                    String key = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.event-metadata.@event-key");

                    LOGGER.info("Date/Fixture/Key : " + date + "/" + fixture + "/" + key);

                    if (fixture != null && key != null) {
                        String feedResponse = fp.processFeed(fixture, key);
                        HashMap<String, Object> feedSynMap = new ObjectMapper().readValue(feedResponse, HashMap.class);
                        String dateRecieved = (String) MapUtil.get(feedSynMap, "$.data.sports-content.sports-metadata.@date-time");

                        LOGGER.info("Date/ReceivedDate : " + date + "/" + dateRecieved);
                        if (!dateRecieved.equals(date)) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                            Date dateResult1 = dateFormat.parse(date);
                            Date dateResult2 = dateFormat.parse(dateRecieved);

                            Long secondsDiff = TimeUnit.MILLISECONDS.toSeconds(dateResult2.getTime() - dateResult1.getTime());

                            LOGGER.warn("Document not updated yet, date Actual/FeedSyn : " + date + "/" + dateRecieved + " == Fixture/Key : " + fixture + "/" + key);
                            LOGGER.warn("Last update delay (in seconds) :" + secondsDiff);

                            if (secondsDiff > 30) {
                                LOGGER.error("Document not updated after 30 seconds Fixture/Key : " + fixture + "/" + key);
                            }
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error("IOException processing feeds", e);
                } catch (Exception e) {
                    LOGGER.error("Exception processing feeds", e);
                }
            }
        }
    }
}
