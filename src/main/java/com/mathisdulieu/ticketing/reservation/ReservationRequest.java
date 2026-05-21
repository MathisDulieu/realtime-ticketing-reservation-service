package com.mathisdulieu.ticketing.reservation;

import lombok.Builder;

@Builder
public record ReservationRequest(
        String eventId,
        String userId
) {}
