package net.dima_community.CommunityProject.config;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.service.chat.ChatRoomService;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final ChatRoomService chatRoomService;

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
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());

        // 세션 속성이 null인지 확인
        if (headers.getSessionAttributes() == null) {
            return; // 세션 속성이 없으면 메서드를 종료
        }

        String userId = headers.getUser().getName();
        Long roomId = (Long) headers.getSessionAttributes().get("roomId");

        if (roomId != null) {
            chatRoomService.removeUserFromRoom(roomId, userId);
        }
    }
}