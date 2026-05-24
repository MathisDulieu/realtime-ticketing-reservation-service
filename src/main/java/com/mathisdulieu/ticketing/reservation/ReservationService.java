package com.mathisdulieu.ticketing.reservation;

import com.mathisdulieu.ticketing.library.core.dto.ReservationEvent;
import com.mathisdulieu.ticketing.library.core.utils.UuidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationEventProducer reservationEventProducer;
    private final UuidService uuidService;

    public void createReservation(ReservationRequest reservationRequest) {
        Reservation reservation = Reservation.builder()
                .id(uuidService.generateUuid())
                .userId(reservationRequest.userId())
                .eventId(reservationRequest.eventId())
                .build();

        reservationRepository.save(reservation);
        log.info("Reservation saved: id={}", reservation.id());

        ReservationEvent reservationEvent = buildReservationEvent(reservation.id());

        reservationEventProducer.sendReservationCreatedEvent(reservationEvent);
    }

    public void cancelReservation(String reservationId) {
        reservationRepository.deleteById(reservationId);
        log.info("Reservation deleted: id={}", reservationId);

        ReservationEvent reservationEvent = buildReservationEvent(reservationId);

        reservationEventProducer.sendReservationCancelledEvent(reservationEvent);
    }

    private static ReservationEvent buildReservationEvent(final String eventId) {
        return ReservationEvent.builder()
                .eventId(eventId)
                .build();
    }

}
