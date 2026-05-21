package com.mathisdulieu.ticketing.reservation;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jmx.export.MBeanExporter;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class RealtimeTicketingReservationServiceConfigurationTests {

    @Bean
    public MBeanExporter exporter() {
        return mock(MBeanExporter.class);
    }

    @Bean(destroyMethod = "shutdown")
    public MongoServer mongoServer() {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());
        mongoServer.bind();
        return mongoServer;
    }

    @Primary
    @Bean(destroyMethod = "close")
    public MongoClient mongoClient(MongoServer mongoServer) {
        return MongoClients.create("mongodb://" + mongoServer.getLocalAddress().getHostName() + ":" + mongoServer.getLocalAddress().getPort());
    }

}
