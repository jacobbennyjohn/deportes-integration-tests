package com.univision.feedsyn.utils;

import java.util.HashMap;

/**
 * Generate Signature
 * Created by jbjohn on 9/15/15.
 */
public class SignatureGenerator {
    private static final String CLIENT_ID = "7c7d44d3f3de1fb93c2a035e916c6774010ea9fc";
    private static final String SECRET = "bdccbf7173492babf02e570a6ba19725c8c0b90d";

    public static String generateSignature(String url) {
        String signature = null;
        String httpVerb = "GET";
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("client_id", CLIENT_ID);
        signature = SignatureUtils.generateSignature(httpVerb, url, queryParams, null, CLIENT_ID, SECRET);
        return signature;
    }

    public static String getClientId() {
        return CLIENT_ID;
    }
}