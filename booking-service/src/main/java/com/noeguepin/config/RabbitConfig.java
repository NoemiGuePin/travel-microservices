package com.noeguepin.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
	
	  @Bean
	  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
	    return new Jackson2JsonMessageConverter();
	  }

	  @Bean
	  public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory cf,
	                                       Jackson2JsonMessageConverter m) {
	    RabbitTemplate tpl = new RabbitTemplate(cf);
	    tpl.setMessageConverter(m);
	    return tpl;
	  }

	  @Bean
	  public TopicExchange notificationsExchange() {
	    return new TopicExchange("notifications.exchange", true, false);
	  }
}
