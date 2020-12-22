package com.vsvdev.rabbitexcanger;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitexcangerApplication {

    static final String TOPIC_EXCHANGER_NAME = "topicExchanger";

    private final com.vsvdev.rabbitexcanger.Receiver receiver;

    public RabbitexcangerApplication(com.vsvdev.rabbitexcanger.Receiver receiver) {
        this.receiver = receiver;
    }

    @Bean
    Queue queueTopic1() {
        return new Queue("Client 1", false);
    }

    @Bean
    Queue queueTopic2() {
        return new Queue("Client 2", false);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGER_NAME);
    }

    @Bean
    Binding bindingTopic1(@Qualifier("queueTopic1") Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with("foo.bar.#");
    }

    @Bean
    Binding bindingTopic2(@Qualifier("queueTopic2") Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with("foo.bar.#");
    }

    @Bean
    SimpleMessageListenerContainer containerForTopic(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("Client 1", "Client 2");
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }



    public static void main(String[] args) {
        SpringApplication.run(RabbitexcangerApplication.class, args);
    }

}
