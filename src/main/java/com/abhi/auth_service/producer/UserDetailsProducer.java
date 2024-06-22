package com.abhi.auth_service.producer;

import com.abhi.auth_service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsProducer {
    @Value("${rabbitmq.exchanges.auth-user}")
    private String authUserExchange;

    @Value("${rabbitmq.routing-keys.auth-user}")
    private String authUserRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(UserDTO message) {
        try {
            rabbitTemplate.convertAndSend(authUserExchange, authUserRoutingKey, message);
            log.info("Message sent successfully: {}", message);
        } catch (AmqpException e) {
            log.error("Failed to send message: {}", message, e);
        }
    }
}