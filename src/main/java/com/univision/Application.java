package com.univision;

import com.univision.properties.FeedsynProperties;
import com.univision.properties.XmlteamProperties;
import com.univision.validator.FeedValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        SpringApplication.exit(ctx);
    }

    @Autowired
    private FeedsynProperties feedsyn;

    @Autowired
    private XmlteamProperties xmlteam;
    
    @Override
    public void run(String... strings) throws Exception {
        FeedValidator feedValidator = new FeedValidator();
        feedValidator.freshnessCheck(feedsyn.getUrl(), xmlteam.getManifest(), xmlteam.getBaseurl());
    }
}
