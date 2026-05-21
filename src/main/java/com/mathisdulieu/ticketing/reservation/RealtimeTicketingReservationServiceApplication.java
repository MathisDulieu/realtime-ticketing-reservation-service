package com.mathisdulieu.ticketing.reservation;

import com.mathisdulieu.ticketing.reservation.config.CorsProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Date;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.mathisdulieu.ticketing.reservation"})
@ConfigurationPropertiesScan
@EnableConfigurationProperties({
    CorsProperties.class
})
public class RealtimeTicketingReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealtimeTicketingReservationServiceApplication.class, args);
    }

    @PostConstruct
    void steUtcTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        log.info("RealtimeTicketingReservationServiceApplication running in UTC timezone at : {}", new Date());
    }

    @Configuration
    @Profile("test")
    @ComponentScan(lazyInit = true)
    static class ConfigForShorterBootTimeForTests {
    }
}
