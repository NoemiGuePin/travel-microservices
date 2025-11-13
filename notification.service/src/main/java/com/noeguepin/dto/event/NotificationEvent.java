package com.noeguepin.dto.event;

import java.time.OffsetDateTime;

public record NotificationEvent<T>(
		  String eventId,
		  String eventType,
		  OffsetDateTime timestamp,
		  String correlationId,
		  T payload) {

}
