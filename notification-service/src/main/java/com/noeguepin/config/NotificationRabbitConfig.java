package com.noeguepin.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableRabbit
@Configuration
public class NotificationRabbitConfig {
	
	@Value("${app.exchange}")
    public String exchange_name;
	@Value("${app.queues.reservationCreated}")
    public String queue_name_created;
	@Value("${app.queues.reservationCancelled}")
    public String queue_name_cancelled;
	@Value("${app.queues.reservationUpdated}")
    public String queue_name_updated;
	@Value("${app.routingKey.reservationCreated}")
    public String routing_key_created;
	@Value("${app.routingKey.reservationCancelled}")
    public String routing_key_cancelled;
	@Value("${app.routingKey.reservationUpdated}")
    public String routing_key_updated;

    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange(exchange_name, true, false);
    }

    @Bean
    public Queue reservationCreatedQueue() {
        return new Queue(queue_name_created, true);
    }
    
    @Bean
    public Queue reservationCancelledQueue() {
      return new Queue(queue_name_cancelled, true);
    }
    
    @Bean
    public Queue reservationUpdatedQueue() {
      return new Queue(queue_name_updated, true);
    }

    @Bean
    public Binding reservationCreatedBinding(TopicExchange notificationsExchange,
                                             Queue reservationCreatedQueue) {
        return BindingBuilder
                .bind(reservationCreatedQueue)
                .to(notificationsExchange)
                .with(routing_key_created);
    }
    
    @Bean
    public Binding reservationCancelledBinding(TopicExchange notificationsExchange,
                                               Queue reservationCancelledQueue) {
      return BindingBuilder.
    		  bind(reservationCancelledQueue)
    		  .to(notificationsExchange)
    		  .with(routing_key_cancelled);
    } 
    
    @Bean
    public Binding reservationUpdatedBinding(TopicExchange notificationsExchange,
                                             Queue reservationUpdatedQueue) {
      return BindingBuilder.
    		  bind(reservationUpdatedQueue)
    		  .to(notificationsExchange)
    		  .with(routing_key_updated);
    }
    
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper om) {
        om.findAndRegisterModules();
        return new Jackson2JsonMessageConverter(om);
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter conv) {

        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(conv);
        return f;
    }
}
