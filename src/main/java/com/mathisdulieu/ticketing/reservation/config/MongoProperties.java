package com.mathisdulieu.ticketing.reservation.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import static org.springframework.util.Assert.hasText;

@Profile("!test")
@ConfigurationProperties(prefix = "reservation.mongodb")
public record MongoProperties(
    String uri,
    String databaseName
) implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        hasText(uri, "reservation.mongodb.uri must be given");
        hasText(databaseName, "reservation.mongodb.databaseName must be given");
    }
}
