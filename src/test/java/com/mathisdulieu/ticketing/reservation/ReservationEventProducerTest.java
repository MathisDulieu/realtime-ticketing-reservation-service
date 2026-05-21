package com.mathisdulieu.ticketing.reservation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationEventProducerTest {

    @Mock
    private KafkaTemplate<String, ReservationEvent> kafkaTemplate;

    @InjectMocks
    private ReservationEventProducer reservationEventProducer;

    @Test
    void shouldSendReservationCreatedEvent() {
        // Arrange
        ReservationEvent reservationEvent = ReservationEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        reservationEventProducer.sendReservationCreatedEvent(reservationEvent);

        // Assert
        verify(kafkaTemplate).send("json_reservation_created", reservationEvent);
    }

    @Test
    void shouldSendReservationCancelledEvent() {
        // Arrange
        ReservationEvent reservationEvent = ReservationEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        reservationEventProducer.sendReservationCancelledEvent(reservationEvent);

        // Assert
        verify(kafkaTemplate).send("json_reservation_cancelled", reservationEvent);
    }

}
