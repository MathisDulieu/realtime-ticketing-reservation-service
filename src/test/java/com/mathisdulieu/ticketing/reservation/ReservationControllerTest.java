package com.mathisdulieu.ticketing.reservation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void shouldCreateReservation() throws Exception {
        // Arrange

        // Act
        ResultActions resultActions = mockMvc.perform(post("/api/v1/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "eventId": "event-id",
                    "userId": "user-id"
                }
                """));

        // Assert
        ReservationRequest expectedReservationRequest = ReservationRequest.builder()
            .eventId("event-id")
            .userId("user-id")
            .build();

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(content().string("Reservation created"));
        verify(reservationService).createReservation(expectedReservationRequest);
    }

    @Test
    void shouldCancelReservation() throws Exception {
        // Arrange
        String reservationId = "reservation-id";

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/reservations/{id}", reservationId));

        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(content().string("Reservation cancelled"));
        verify(reservationService).cancelReservation(reservationId);
    }

}
