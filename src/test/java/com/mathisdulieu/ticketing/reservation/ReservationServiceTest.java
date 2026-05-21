package com.mathisdulieu.ticketing.reservation;

import com.mathisdulieu.ticketing.reservation.utils.UuidService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationEventProducer reservationEventProducer;
    @Mock
    private UuidService uuidService;
    @InjectMocks
    private ReservationService reservationService;

    @Test
    void shouldCreateReservation() {
        // Arrange
        ReservationRequest reservationRequest = ReservationRequest.builder()
            .eventId("event-id")
            .userId("user-id")
            .build();

        when(uuidService.generateUuid()).thenReturn("reservation-uuid");

        // Act
        reservationService.createReservation(reservationRequest);

        // Assert
        Reservation reservation = Reservation.builder()
            .id("reservation-uuid")
            .eventId("event-id")
            .userId("user-id")
            .build();

        ReservationEvent reservationEvent = ReservationEvent.builder()
            .eventId("reservation-uuid")
            .build();

        verify(reservationEventProducer).sendReservationCreatedEvent(reservationEvent);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldCancelReservation() {
        // Arrange
        String reservationId = "reservation-id";

        // Act
        reservationService.cancelReservation(reservationId);

        // Assert
        ReservationEvent reservationEvent = ReservationEvent.builder()
            .eventId("reservation-id")
            .build();

        verify(reservationRepository).deleteById(reservationId);
        verify(reservationEventProducer).sendReservationCancelledEvent(reservationEvent);
    }

}
