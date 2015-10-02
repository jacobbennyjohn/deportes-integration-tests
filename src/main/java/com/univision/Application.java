package com.univision;

import com.univision.validator.FeedValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        FeedValidator.freshnessCheck();
        SpringApplication.exit(ctx);
    }
}
