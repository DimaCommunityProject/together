package net.dima_community.CommunityProject.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.chat.ChatMessage;
import net.dima_community.CommunityProject.service.chat.ChatRoomService;
import net.dima_community.CommunityProject.service.chat.ChatService;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());

        // 세션 속성이 null인지 확인
        if (headers.getSessionAttributes() == null) {
            return; // 세션 속성이 없으면 메서드를 종료
        }

        String userId = headers.getUser().getName();
        Long roomId = (Long) headers.getSessionAttributes().get("roomId");

        if (roomId != null) {
            chatRoomService.addUserToRoom(roomId, userId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");

        if (userId != null) {
            // 유저를 오프라인으로 설정하고 퇴장 메시지를 처리
            chatService.setUserOnlineStatus(userId, false);
            log.info("User disconnected: " + userId);

            // 유저가 퇴장한 방에 대한 정보를 얻고, 퇴장 메시지를 보낼 수 있음
            Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");
            if (roomId != null) {
                ChatMessage message = new ChatMessage();
                message.setSenderId(userId);
                message.setRoomId(roomId);
                message.setContent(userId + "님이 퇴장하셨습니다.");
                message.setTimestamp(LocalDateTime.now().toString());

                // RabbitMQ로 퇴장 메시지 전송
                chatService.notifyUsersInRoom(roomId);
            }
        }
    }
}