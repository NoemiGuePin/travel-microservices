package com.noeguepin.service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.noeguepin.dto.events.NotificationEvent;
import com.noeguepin.dto.events.NotificationEventType;
import com.noeguepin.dto.events.ReservationCancelledPayload;
import com.noeguepin.dto.events.ReservationCreatedPayload;
import com.noeguepin.dto.events.ReservationUpdatedPayload;
import com.noeguepin.dto.events.UserInfo;
import com.noeguepin.model.Reservation;

@Component
public class NotificationEventPublisher {
	
	  private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
	  private final RabbitTemplate rabbitTemplate;
	  private final String notificationExchangeName;
	  private final String reservationCreatedRoutingKey;
	  private final String reservationCancelledRoutingKey;
	  private final String reservationUpdatedRoutingKey;
	  
	  public NotificationEventPublisher(
		        RabbitTemplate rabbitTemplate,
		        @Value("${app.notifications.exchange}") String notificationExchangeName,
		        @Value("${app.notifications.routingKey.reservationCreated}") String reservationCreatedRoutingKey,
		        @Value("${app.notifications.routingKey.reservationCancelled}") String reservationCancelledRoutingKey,
		        @Value("${app.notifications.routingKey.reservationUpdated}") String reservationCancelledUpdatedRoutingKey
			  ) {

		        this.rabbitTemplate = rabbitTemplate;
		        this.notificationExchangeName = notificationExchangeName;
		        this.reservationCreatedRoutingKey = reservationCreatedRoutingKey;
		        this.reservationCancelledRoutingKey = reservationCancelledRoutingKey;
		        this.reservationUpdatedRoutingKey = reservationCancelledUpdatedRoutingKey;
	  }

	  public void publishReservationCreated(String bookingCode, String flightCode, int passengersNumber, UserInfo userInfo) {
		  
	    String eventId = UUID.randomUUID().toString();
	    String correlationId = getCorrelationId();    
	    var payload = new ReservationCreatedPayload(bookingCode, flightCode, 
	    		passengersNumber, userInfo);
	    
	    var event = new NotificationEvent<>(
	            eventId,
	            NotificationEventType.RESERVATION_CREATED.value(),
	            OffsetDateTime.now(),
	            correlationId,
	            payload
	    );

	    rabbitTemplate.convertAndSend(notificationExchangeName, reservationCreatedRoutingKey, event, message -> {
	    	message.getMessageProperties().setMessageId(eventId);
	    	message.getMessageProperties().setHeader("correlationId", correlationId);
	    	return message;
	    });
	  }
	  
	  public void publishReservationCancelled(String bookingCode, UserInfo userInfo) {
		  
		  String eventId = UUID.randomUUID().toString();
		  String correlationId = getCorrelationId();	  
		  var payload = new ReservationCancelledPayload(bookingCode, userInfo);
		  
		  var event = new NotificationEvent<>(
				  eventId, 
				  NotificationEventType.RESERVATION_CANCELLED.value(), 
				  OffsetDateTime.now(), 
				  correlationId, 
				  payload
		 );
		  
		 rabbitTemplate.convertAndSend(notificationExchangeName, reservationCancelledRoutingKey, event, message -> {
			 message.getMessageProperties().setMessageId(eventId);
			 message.getMessageProperties().setHeader("correlationId", correlationId);
			 return message;
		 });
		 
		 log.info("Intento de publicación del evento CANCELLED con ID: {} al Exchange: {} con Key: {}.", 
	               eventId, notificationExchangeName, reservationCancelledRoutingKey);
	  } 
	  
	  public void publishReservationUpdated(String bookingCode, String flightCode, int passengersNumber, UserInfo userInfo) {
		  
		  String eventId = UUID.randomUUID().toString();
		  String correlationId = getCorrelationId();	  
		  var payload = new ReservationUpdatedPayload(bookingCode, flightCode, passengersNumber, userInfo);
		  
		  var event = new NotificationEvent<>(
				  eventId, 
				  NotificationEventType.RESERVATION_UPDATED.value(), 
				  OffsetDateTime.now(),
				  correlationId,
				  payload
		 );
		  
		 rabbitTemplate.convertAndSend(notificationExchangeName, reservationUpdatedRoutingKey, event, message -> {
			 message.getMessageProperties().setMessageId(eventId);
			 message.getMessageProperties().setHeader("correlationId", correlationId);
			 return message;
		 });
	  } 
	  
	  private String getCorrelationId() {
		    String correlationId = MDC.get("correlationId");
		    return (correlationId != null) ? correlationId : UUID.randomUUID().toString();
		}
}
