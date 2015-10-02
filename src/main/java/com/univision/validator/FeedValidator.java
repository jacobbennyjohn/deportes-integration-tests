package com.univision.validator;

import com.univision.xmlteam.ManifestReader;
import com.univision.xmlteam.Normalizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by jbjohn on 10/2/15.
 */
public class FeedValidator {

    public static void freshnessCheck() {
        /**
         * 1. Check the manifest from xml team
         * 2. Fetch the files that are older than 30 seconds
         * 3. Remove duplicates
         * 4. Generate Feed syn url for the feeds objects
         * 5. Validate the feed against the feedsyn response
         */
        String manifestUrl = "http://feed5.xmlteam.com/api/feeds?start=PT2M&format=xml&sport-keys=15054000";
        ManifestReader manifestReader = new ManifestReader();
        List<String> urlList = manifestReader.fetchLinksAndProcess(manifestUrl);
        if (urlList != null) {
            for (String url : urlList) {
                try {
                    String feedResponse = manifestReader.getXMLTeamURL(url);
                    Normalizer normalizer = new Normalizer();
                    String response = normalizer.normalize(new ByteArrayInputStream(feedResponse.getBytes(StandardCharsets.UTF_8)));

                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
