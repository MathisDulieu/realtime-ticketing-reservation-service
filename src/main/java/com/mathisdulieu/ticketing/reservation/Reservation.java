package com.mathisdulieu.ticketing.reservation;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document(collection = "reservations")
public record Reservation(
        @Id String id,
        String eventId,
        String userId
) {}
