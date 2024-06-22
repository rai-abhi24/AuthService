package com.abhi.auth_service.consumer;

import com.abhi.auth_service.dto.UserDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsConsumer {

    @RabbitListener(queues = "${rabbitmq.queues.auth-user}")
    public void consumer(UserDTO message) {
        System.out.printf("Received message => %s%n", message);
    }
}
