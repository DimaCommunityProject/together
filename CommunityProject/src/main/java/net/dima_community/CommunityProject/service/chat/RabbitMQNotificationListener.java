package net.dima_community.CommunityProject.service.chat;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQNotificationListener {

    private final SimpMessagingTemplate messagingTemplate;

    public RabbitMQNotificationListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 메시지 수신 시 알림 전송
    @RabbitListener(queues = "chat.queue")
    public void receiveMessage(String message) {
    	System.out.println("Received message: " + message);
        messagingTemplate.convertAndSend("/topic/notifications", "새로운 채팅 메시지가 도착했습니다!");
    }
}

