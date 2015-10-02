package com.univision.feedsyn;

import com.univision.feedsyn.utils.SignatureGenerator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
/**
 * Created by jbjohn on 10/2/15.
 */
public class FeedProcessor {

    public String processFeed(String type, String gameId) throws URISyntaxException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet();
        String url = "/feed/sports/soccer/";
        switch (type) {
            case "commentary":
                url = url + "event-commentary/" + gameId;
                break;
            case "stats-live":
                url = url + "event-stats-progressive/" + gameId;
                break;
            case "stats":
                url = url + "event-stats/" + gameId;
                break;
            case "schedule":
                url = url + "schedule-results/" + gameId;
                break;
            default:
                break;
        }
        String signature = SignatureGenerator.generateSignature(url);
        url = "http://sports.dev.y.univision.com" + url + "?client_id=" + SignatureGenerator.getClientId() + "&signature=" + signature;
        URI uri = new URI(url);
        System.out.println(url);
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