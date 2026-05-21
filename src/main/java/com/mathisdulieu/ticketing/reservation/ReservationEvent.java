package com.mathisdulieu.ticketing.reservation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationEvent(
        String eventId
) {
}
