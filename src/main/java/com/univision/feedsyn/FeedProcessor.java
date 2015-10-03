package com.univision.feedsyn;

import com.univision.feedsyn.utils.SignatureGenerator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
/**
 */
public class FeedProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedProcessor.class);
    
    private String fsynUrl;
    
    public FeedProcessor(String fsynUrl) {
    	this.fsynUrl = fsynUrl;
    }

    public String processFeed(String type, String gameId) throws URISyntaxException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet();
        String url = "/feed/sports/soccer/";
        switch (type) {
            case "event-commentary":
                url = url + "event-commentary/" + gameId;
                break;
            case "event-stats":
            case "event-stats-progressive":
                url = url + "event-stats/" + gameId;
                break;
            case "schedule":
                url = url + "schedule-results/" + gameId;
                break;
            default:
                break;
        }
        String signature = SignatureGenerator.generateSignature(url);
        url = fsynUrl + url + "?client_id=" + SignatureGenerator.getClientId() + "&signature=" + signature;
        URI uri = new URI(url);
        LOGGER.info("Processing : " + url);
        httpGet.setURI(uri);
        httpGet.setHeader("Authorization", "Basic ZGVidWc6WG9vbmcxZWU=");
        HttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
        return null;
    }
}