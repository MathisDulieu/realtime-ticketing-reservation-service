package com.mathisdulieu.ticketing.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventProducer {

    private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;

    private static final String RESERVATION_CREATED_TOPIC = "json_reservation_created";
    private static final String RESERVATION_CANCELLED_TOPIC = "json_reservation_cancelled";

    public void sendReservationCreatedEvent(final ReservationEvent reservationEvent) {
        log.debug("Send reservation created event with eventId: {}", reservationEvent.eventId());
        kafkaTemplate.send(RESERVATION_CREATED_TOPIC, reservationEvent);
    }

    public void sendReservationCancelledEvent(final ReservationEvent reservationEvent) {
        log.debug("Send reservation cancelled event with eventId: {}", reservationEvent.eventId());
        kafkaTemplate.send(RESERVATION_CANCELLED_TOPIC, reservationEvent);
    }
}
