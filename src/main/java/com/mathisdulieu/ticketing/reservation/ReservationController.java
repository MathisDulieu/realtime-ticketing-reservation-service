package com.mathisdulieu.ticketing.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a reservation", description = "Creates a new reservation")
    @ApiResponse(responseCode = "200", description = "Reservation created successfully")
    public ResponseEntity<String> createReservation(@RequestBody final ReservationRequest reservationRequest) {
        log.debug("Received request: POST /api/v1/reservations");
        reservationService.createReservation(reservationRequest);
        return ResponseEntity.ok("Reservation created");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a reservation", description = "Cancels an existing reservation")
    @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully")
    public ResponseEntity<String> cancelReservation(@PathVariable final String id) {
        log.debug("Received request: DELETE /api/v1/reservations/{}", id);
        reservationService.cancelReservation(id);
        return ResponseEntity.ok("Reservation cancelled");
    }
}
