package com.univision.xmlteam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jbjohn.MapUtil;
import com.jbjohn.model.Caster;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.HashMap;

/**
 */
public class Normalizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Normalizer.class);

    public String normalize(InputStream xml) throws IOException {

        InputStream xsl1 = this.getClass().getClassLoader().getResourceAsStream("xsl/xmlteam/bbc-to-xts.xsl");
        String response1 = transformer(xml, xsl1);

        InputStream xml2 = new ByteArrayInputStream(response1.getBytes());
        InputStream xsl2 = this.getClass().getClassLoader().getResourceAsStream("xsl/xmlteam/xts-to-2.2.xsl");
        String response2 = transformer(xml2, xsl2);

        InputStream xml3 = new ByteArrayInputStream(response2.getBytes());
        InputStream xsl3 = this.getClass().getClassLoader().getResourceAsStream("xsl/univision/normalize.xsl");
        String response3 = transformer(xml3, xsl3);

        InputStream xml4 = new ByteArrayInputStream(response3.getBytes());
        InputStream xsl4 = this.getClass().getClassLoader().getResourceAsStream("xsl/univision/processingInstructions.xsl");
        String response4 = transformer(xml4, xsl4);

        String json = "";
        try {
            json = xmlToJson(response4, true);
        } catch (XMLStreamException e) {
            LOGGER.error("Exception", e);
        }

        HashMap<String,Object> result = new ObjectMapper().readValue(json, HashMap.class);

        MapUtil.parse(result, "$.sports-content.sports-event.team.[*].player.[*].player-metadata.player-metadata-soccer.@line-formation-position", Caster.Type.INTEGER);
        MapUtil.parse(result, "$.sports-content.sports-event.team.[*].player.[*].player-metadata.player-metadata-soccer.@line-formation", Caster.Type.INTEGER);
        MapUtil.parse(result, "$.sports-content.sports-event.team.[*].player.[*].player-metadata.player-metadata-soccer.@time-entered-event", Caster.Type.INTEGER);
        MapUtil.parse(result, "$.sports-content.sports-event.team.[*].player.[*].player-metadata.player-metadata-soccer.@time-exited-event", Caster.Type.INTEGER);

        Gson gson = new Gson();
        String processedJson = gson.toJson(result);

        return processedJson;
    }

    public static String transformer(InputStream xml, InputStream xsl) {
        StreamSource stylesource = new StreamSource(xsl);
        TransformerFactory factory = TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer;

        try {

            StreamSource source = new StreamSource(xml);
            transformer = factory.newTransformer(stylesource);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            String response = writer.toString();
            return response;

        } catch (TransformerConfigurationException ex) {
            LOGGER.error("Exception", ex);
        } catch (TransformerException ex) {
            LOGGER.error("Exception", ex);
        }
        return "";
    }

    public static String xmlToJson(String xml, boolean formatted) throws XMLStreamException {
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        JsonXMLConfig config = new JsonXMLConfigBuilder()
                .autoArray(true)
                .autoPrimitive(true)
                .prettyPrint(formatted)
                .build();

        try {
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);
            XMLEventWriter writer = new JsonXMLOutputFactory(config).createXMLEventWriter(output);
            writer.add(reader);
            reader.close();
            writer.close();
            try {
                return output.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Exception", e);
                throw new XMLStreamException(e.getMessage());
            }
        } finally {
            // do nothing
        }
    }
}
