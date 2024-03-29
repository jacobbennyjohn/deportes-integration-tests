package com.univision.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbjohn.MapUtil;
import com.univision.EventRepository;
import com.univision.InformationRepository;
import com.univision.feedsyn.FeedProcessor;
import com.univision.storage.Information;
import com.univision.storage.Record;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 */
@Component
public class FeedValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedValidator.class);

    private Long notificationTtl = 30L;

    private EventRepository storage;

    private InformationRepository info;

    public void setNotificationTtl(Long notificationTtl) {
        this.notificationTtl = notificationTtl;
    }

    public void setStorage(EventRepository storage) {
        this.storage = storage;
    }

    public void setInfo(InformationRepository info) {
        this.info = info;
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
                    String eventStatus = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.event-metadata.@event-status");
                    String tournamentId = (String) MapUtil.get(jsonMap, "$.sports-content.sports-metadata.sports-content-codes.sports-content-code.[?@code-type==tournament].@code-key.[0]");

                    Map<String, String> additionalQueryParams = new HashMap<>();

                    if (fixture.equalsIgnoreCase("standings")) {
                        key = tournamentId;
                        String season = (String) MapUtil.get(jsonMap, "$.sports-content.sports-metadata.sports-content-codes.sports-content-code.[?@code-type==season].@code-key.[0]");

                        additionalQueryParams.put("leagueKey", key);
                        additionalQueryParams.put("seasonKey", season);
                    }

                    LOGGER.info("Hashcode : " + hashCode + " => " + "Date/Fixture/Key : " + date + "/" + fixture + "/" + key);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                    Date documentDate = dateFormat.parse(date);

                    if (fixture != null && key != null) {

                        Record record = new Record();
                        record.setEventId(key);
                        record.setFixture(fixture);
                        record.setDocDate(documentDate);
                        record.setStatus(Record.Status.MISSING);
                        record.setDelayTime(0L);
                        record.setEventStatus(eventStatus);

                        fp.setAdditionalQueryParams(additionalQueryParams);
                        String feedResponse = fp.processFeed(fixture, key);
                        if (feedResponse != null) {
                            HashMap<String, Object> feedSynMap = new ObjectMapper().readValue(feedResponse, HashMap.class);
                            if (feedSynMap != null) {
                                String status = (String) MapUtil.get(feedSynMap, "$.status");
                                if (status.equals("success")) {
                                    String dateRecieved = (String) MapUtil.get(feedSynMap, "$.data.sports-content.sports-metadata.@date-time");
                                    LOGGER.info("Date/ReceivedDate : " + date + "/" + dateRecieved);
                                    record.setStatus(Record.Status.UPDATED);
                                    if (!dateRecieved.equals(date)) {
                                        Date feedSynDocDate = dateFormat.parse(dateRecieved);
                                        Long lastUpdateDelay = TimeUnit.MILLISECONDS.toSeconds(documentDate.getTime() - feedSynDocDate.getTime());

                                        if (lastUpdateDelay > 0) {

                                            LOGGER.warn("Hashcode : " + hashCode + " => " + "Document not updated yet, date Actual/FeedSyn : " + date + "/" + dateRecieved + " == Fixture/Key : " + fixture + "/" + key);
                                            LOGGER.warn("Hashcode : " + hashCode + " => " + "Last update delay (in seconds) :" + lastUpdateDelay);

                                            if (lastUpdateDelay > notificationTtl) {
                                                LOGGER.error("Hashcode : " + hashCode + " => " + "Document not updated after " + lastUpdateDelay + " seconds, Fixture/Key : " + fixture + "/" + key);
                                            }

                                            record.setStatus(Record.Status.DELAYED);
                                            record.setDelayTime(lastUpdateDelay);
                                        } else {
                                            LOGGER.info("Hashcode : " + hashCode + " => " + " time difference : " + lastUpdateDelay.toString());
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

                        record.setId();
                        LOGGER.info("Record object as json : " + record.toString());
                        this.storage.save(record);

                        if (fixture.equalsIgnoreCase("event-stats") || fixture.equalsIgnoreCase("event-stats-progressive")) {

                            Map<String, String> map = new HashMap<>();
                            String leagueName = (String) MapUtil.get(jsonMap, "$.sports-content.sports-metadata.sports-content-codes.sports-content-code.[?@code-type==league].@code-name.[0]");

                            String teamHome = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.team.[0].team-metadata.name.@full");
                            String teamAway = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.team.[1].team-metadata.name.@full");

                            String teamHomeKey = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.team.[0].team-metadata.@team-key");
                            String teamAwayKey = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.team.[1].team-metadata.@team-key");

                            if (!eventStatus.equals("pre-event")) {

                                String teamHomeScore = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.team.[0].team-stats.@score");
                                String teamAwayScore = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.team.[1].team-stats.@score");
                                String timeElapsed = (String) MapUtil.get(jsonMap, "$.sports-content.sports-event.event-metadata.event-metadata-soccer.@minutes-elapsed");

                                map.put("HomeTeamScore", teamHomeScore);
                                map.put("AwayTeamScore", teamAwayScore);
                                map.put("TimeElapsed", timeElapsed);
                            }

                            map.put("League", leagueName);
                            map.put("HomeTeam", teamHome);
                            map.put("AwayTeam", teamAway);
                            map.put("leagueId", tournamentId);
                            map.put("teamHomeKey", teamHomeKey);
                            map.put("teamAwayKey", teamAwayKey);

                            storeInformation(record.getId(), record.getFixture(), map);
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

    private void storeInformation(String id, String type, Map<String, String> map) {

        Information information = new Information();
        information.setId(id);
        information.setType(type);
        information.setMap(map);

        this.info.save(information);
    }
}
