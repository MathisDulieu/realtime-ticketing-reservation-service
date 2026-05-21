package com.mathisdulieu.ticketing.reservation;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

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

    @Primary
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "reservation-service-test");
    }

    @Bean
    @ConditionalOnProperty("spring.embedded.kafka.brokers")
    public ConsumerFactory<String, ReservationEvent> testConsumerFactory(Environment environment) {
        JsonDeserializer<ReservationEvent> deserializer = new JsonDeserializer<>(ReservationEvent.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        ), new StringDeserializer(), deserializer);
    }

    @Bean
    @ConditionalOnProperty("spring.embedded.kafka.brokers")
    public ConcurrentKafkaListenerContainerFactory<String, ReservationEvent> kafkaListenerContainerFactory(
        ConsumerFactory<String, ReservationEvent> testConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ReservationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(testConsumerFactory);
        return factory;
    }

}
