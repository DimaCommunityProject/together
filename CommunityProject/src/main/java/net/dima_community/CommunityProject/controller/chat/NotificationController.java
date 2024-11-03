package net.dima_community.CommunityProject.controller.chat;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import net.dima_community.CommunityProject.service.chat.NotificationService;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 알림을 전송하는 API 엔드포인트
    @PostMapping("/sendNotification")
    public void sendNotification(@RequestBody String message) {
        notificationService.sendNotification(message);
    }
}
