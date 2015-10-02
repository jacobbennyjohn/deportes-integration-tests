package com.univision.validator;

import com.univision.feedsyn.FeedProcessor;

import java.io.IOException;
import java.net.URISyntaxException;

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

        FeedProcessor fp = new FeedProcessor();
        try {
            String response = fp.processFeed("stats", "832695");
            System.out.println(response);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
