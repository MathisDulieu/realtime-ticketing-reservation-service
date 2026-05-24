package com.mathisdulieu.ticketing.reservation;

import com.mathisdulieu.ticketing.library.core.dto.ReservationEvent;
import com.mathisdulieu.ticketing.library.test.kafka.consumer.GenericTestKafkaConsumer;
import com.mathisdulieu.ticketing.library.test.mongo.config.MongoTestConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = {"json_realtime_reservation_created", "json_realtime_reservation_cancelled"})
@ActiveProfiles("test")
@Import({
    RealtimeTicketingReservationServiceConfigurationTests.class,
    RealtimeTicketingReservationServiceIntegrationTest.TestKafkaConsumer.class,
    MongoTestConfig.class
})
public class RealtimeTicketingReservationServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestKafkaConsumer testKafkaConsumer;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection("reservations");
        testKafkaConsumer.consumer.clearRecordsFromTopic("json_realtime_reservation_created");
        testKafkaConsumer.consumer.clearRecordsFromTopic("json_realtime_reservation_cancelled");
    }

    @Test
    void shouldCreateReservation() throws InterruptedException {
        // Arrange
        ReservationRequest reservationRequest = ReservationRequest.builder()
            .eventId("event-id")
            .userId("user-id")
            .build();

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/reservations", reservationRequest, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Reservation created");

        ConsumerRecord<String, ReservationEvent> record = testKafkaConsumer.consumer.pollRecord("json_realtime_reservation_created", 5);
        assertThat(record.value().eventId()).isNotBlank();

        List<Reservation> savedReservations = mongoTemplate.findAll(Reservation.class);
        assertThat(savedReservations).hasSize(1);
        assertThat(savedReservations.getFirst().eventId()).isEqualTo("event-id");
        assertThat(savedReservations.getFirst().userId()).isEqualTo("user-id");

        assertThat(record.value().eventId()).isEqualTo(savedReservations.getFirst().id());
    }

    @Test
    void shouldCancelReservation() throws InterruptedException {
        // Arrange
        mongoTemplate.save("""
            {
                "_id": "reservation-id",
                "eventId": "event-id",
                "userId": "user-id"
            }
            """, "reservations");

        // Act
        restTemplate.delete("/api/v1/reservations/{id}", "reservation-id");

        // Assert
        ConsumerRecord<String, ReservationEvent> record = testKafkaConsumer.consumer.pollRecord("json_realtime_reservation_cancelled", 5);
        assertThat(record.value().eventId()).isEqualTo("reservation-id");

        assertThat(mongoTemplate.findAll(Reservation.class)).isEmpty();
    }

    @TestComponent
    static class TestKafkaConsumer {
        final GenericTestKafkaConsumer<ReservationEvent> consumer = new GenericTestKafkaConsumer<>();

        @KafkaListener(
            topics = {
                "json_realtime_reservation_created",
                "json_realtime_reservation_cancelled"
            },
            groupId = "test-group",
            containerFactory = "reservationEventKafkaListenerContainerFactory"
        )
        public void consume(ConsumerRecord<String, ReservationEvent> record) {
            consumer.records.add(record);
        }
    }

}
