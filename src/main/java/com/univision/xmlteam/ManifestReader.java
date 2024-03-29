package com.univision.xmlteam;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

/**
 * ManifestReader
 */
public class ManifestReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManifestReader.class);

    /**
     * Go through the manifest and recursively call next page if more items exist.
     * @param manifest the XML team manifest
     * @return a listing of relative document paths for SportsML content.
     */
    private List<String> getSportsMLDocURLs(String manifest) {

        List<String> urls = new ArrayList<>();
        try {
            // Convert to a document.
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(manifest.getBytes()));

            // Pull all the file paths from the manifest.
            XPath xPath =  XPathFactory.newInstance().newXPath();
            String pathExpression = "//document-listing/file-path";
            NodeList nodeList = null;
            nodeList = (NodeList) xPath.compile(pathExpression).evaluate(document, XPathConstants.NODESET);

            // Go through the manifest and add paths to the list.
            for (int index = 0; index < nodeList.getLength(); index++) {
                Node node = nodeList.item(index);
                urls.add(node.getTextContent());
            }

            // Page through the manifest if there is more.
            String nextExpression = "//metadata/next/text()";
            String next = (String) xPath.compile(nextExpression).evaluate(document, XPathConstants.STRING);

            // Recursively call next page.
            if (next != null && !next.isEmpty()) {
                urls.addAll( getSportsMLDocURLs(getXMLTeamURL(next)) );
            }

        } catch (XPathExpressionException e) {
            LOGGER.error("XPathExpressionException", e);
        } catch (SAXException e) {
            LOGGER.error("SAXException", e);
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        } catch (ParserConfigurationException e) {
            LOGGER.error("ParserConfigurationException", e);
        }

        return urls;
    }

    /**
     * Fetch an XML Team URL with authorization.
     * @param url the URL to fetch.
     * @return a server response body as a string.
     */
    public String getXMLTeamURL(String url) throws IOException {

        LOGGER.info("Manifest url : " + url);

        URI uri = URI.create(url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet();
        httpGet.setHeader("Authorization", "Basic dW5pdmlzaW9uOmM0Ymwz");
        httpGet.setURI(uri);
        HttpResponse response = client.execute(httpGet);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public List<String> fetchLinksAndProcess(String url) {

        String response;
        List<String> feedUrls = null;
        try {
            response = getXMLTeamURL(url);
            feedUrls = getSportsMLDocURLs(response);
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        }

        return feedUrls;
    }
}
