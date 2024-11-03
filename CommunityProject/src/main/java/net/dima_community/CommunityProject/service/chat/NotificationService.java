package net.dima_community.CommunityProject.service.chat;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;

    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private static final String ROUTING_KEY = "room.*";
    
    public void sendNotification(String message) {
    	System.out.println("Sending message: " + message);
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, ROUTING_KEY, message); // ROUTING_KEY를 사용
    }

}
