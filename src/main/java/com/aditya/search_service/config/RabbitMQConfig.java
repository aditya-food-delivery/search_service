package com.aditya.search_service.config;


import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "restaurant.queue";
    public static final String EXCHANGE = "catalog.exchange";
    public static final String ROUTING_KEY = "restaurant.index";

    @PostConstruct
    public void init() {
        System.out.println("RabbitMQConfig Loaded ✅");
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE).build();
    }


    @Bean
    public Binding binding(Queue queue) {
        return BindingBuilder.bind(queue)
                .to(new TopicExchange(EXCHANGE))
                .with(ROUTING_KEY);
    }
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    @Bean
    public CommandLineRunner test(ConnectionFactory connectionFactory) {
        return args -> {
            System.out.println("Search Connected: " +
                    connectionFactory.createConnection().isOpen());
        };
    }



}