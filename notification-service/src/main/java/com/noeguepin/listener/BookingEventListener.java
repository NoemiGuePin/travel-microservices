package com.noeguepin.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.noeguepin.config.NotificationRabbitConfig;
import com.noeguepin.dto.event.NotificationEvent;
import com.noeguepin.dto.event.ReservationCancelledPayload;
import com.noeguepin.dto.event.ReservationCreatedPayload;
import com.noeguepin.dto.event.ReservationUpdatedPayload;
import com.noeguepin.service.EmailService;


@Component
public class BookingEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(BookingEventListener.class);
	
	private final EmailService emailService;
	
	public BookingEventListener(EmailService emailService) {
		this.emailService = emailService;
	}
	
    @RabbitListener(queues = "${app.queues.reservationCreated}")
    public void handleReservationCreated(NotificationEvent<ReservationCreatedPayload> event) {
    	
    	ReservationCreatedPayload payload = event.payload();
    	String to = payload.user().email();
    	String subject = "Reserva confirmada: " + payload.bookingCode();
    	
    	String body = """
    		¡Hola %s!
    		Tu reserva %s del vuelo %s para %d pasajer@s ha sido creada correctamente.
    		""".formatted(payload.user().name(), payload.bookingCode(), payload.flightCode(), payload.passengersNumber());

    	emailService.sendEmail(to, subject, body);
    }  
    
    @RabbitListener(queues = "${app.queues.reservationCancelled}")
    public void handleReservationCancelled(NotificationEvent<ReservationCancelledPayload> event) {
    	
    	ReservationCancelledPayload payload = event.payload();
    	String to = payload.user().email();
    	String subject = "Reserva cancelada: " + payload.bookingCode();
    	
    	String body = """
    		¡Hola %s!
    		Tu reserva %s ha sido cancelada.
    		""".formatted(payload.user().name(), payload.bookingCode());

    	emailService.sendEmail(to, subject, body);
    }  
    
    @RabbitListener(queues = "${app.queues.reservationUpdated}")
    public void handleReservationUpdated(NotificationEvent<ReservationUpdatedPayload> event) {
    	
    	ReservationUpdatedPayload payload = event.payload();
    	String to = payload.user().email();
    	String subject = "Reserva modificada: " + payload.bookingCode();
    	
    	String body = """
    		¡Hola %s!
    		Tu reserva %s del vuelo %s ha sido modificada correctamente. Ahora contiene %d pasajer@s.
    		""".formatted(payload.user().name(), payload.bookingCode(), payload.flightCode(), payload.newPassengersNumber());

    	emailService.sendEmail(to, subject, body);
    } 
}
