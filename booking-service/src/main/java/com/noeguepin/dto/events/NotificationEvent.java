package com.noeguepin.dto.events;

import java.time.OffsetDateTime;

public record NotificationEvent<T>(
		  String eventId,
		  String eventType,
		  OffsetDateTime timestamp,
		  String correlationId,
		  T payload) {

}
