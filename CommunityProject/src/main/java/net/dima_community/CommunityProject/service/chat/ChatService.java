package net.dima_community.CommunityProject.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.chat.ChatMessage;
import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;
import net.dima_community.CommunityProject.repository.chat.ChatMessageRepository;
import net.dima_community.CommunityProject.repository.chat.ChattingRoomMemberRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChatService - 채팅 메시지와 사용자 상태 관리 관련 비즈니스 로직을 담당하는 서비스 클래스.
 * 메시지의 저장, 사용자 상태 관리, RabbitMQ를 통한 메시지 전송을 포함.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
	
	private static final String CHAT_EXCHANGE_NAME = "chat.exchange"; // RabbitMQ 교환기 이름

    private final ChatMessageRepository chatMessageRepository; // 채팅 메시지 저장소
    private final ChattingRoomMemberRepository chattingRoomMemberRepository; // 채팅방 멤버 저장소
    private final RabbitTemplate rabbitTemplate; // RabbitMQ 템플릿
    private final Map<String, Boolean> userStatusMap = new ConcurrentHashMap<>(); // 사용자 상태 관리 맵

    // =======================================================
    // ===================== 채팅 메시지 관리 =====================
    // =======================================================

    /**
     * 특정 채팅방의 메시지 목록을 반환하는 메서드.
     * @param roomId 채팅방 ID
     * @return 메시지 리스트
     */
    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByRoomId(roomId); // 채팅방 ID로 메시지 조회
    }

    /**
     * 채팅 메시지를 데이터베이스에 저장하는 메서드.
     * @param message 채팅 메시지 객체
     * @param roomId 채팅방 ID
     */
    public void saveMessage(ChatMessage message, Long roomId) {
        // 채팅 메시지에 채팅방 ID와 타임스탬프 설정
        message.setRoomId(roomId);
        message.setTimestamp(LocalDateTime.now().toString());
        
        // 메시지 저장소에 메시지 저장
        chatMessageRepository.save(message);
        log.info("Message saved to DB: {}", message);
    }

    // =======================================================
    // ===================== 사용자 상태 관리 =====================
    // =======================================================
    
    /**
     * 사용자의 온라인 상태를 설정하는 메서드.
     * @param userId 사용자 ID
     * @param isOnline 온라인 여부
     */
    public void setUserOnlineStatus(String userId, boolean isOnline) {
        userStatusMap.put(userId, isOnline); // 사용자 상태 맵에 상태 저장
    }

    /**
     * 사용자가 온라인인지 여부를 확인하는 메서드.
     * @param userId 사용자 ID
     * @return 사용자 온라인 상태
     */
    public boolean isUserOnline(String userId) {
        return userStatusMap.getOrDefault(userId, false); // 사용자 상태 조회
    }
    
    // =======================================================
    // ===================== 사용자 상태 알림 =====================
    // =======================================================
    
    /**
     * 특정 채팅방의 모든 사용자들에게 현재 사용자의 상태를 알림.
     * @param chatRoomId 채팅방 ID
     */
    @Transactional
    public void notifyUsersInRoom(Long chatRoomId) {
        // 채팅방의 모든 멤버를 조회
        List<ChattingRoomMemberEntity> members = chattingRoomMemberRepository.findByChatRoomId(chatRoomId);

        // 각 멤버에게 상태 메시지를 전송
        members.forEach(member -> {
            String memberId = member.getMember().getMemberId();
            String memberName = member.getMember().getMemberName();
            boolean isOnline = isUserOnline(memberId);

            // 사용자 상태 메시지 생성
            ChatMessage statusMessage = createStatusMessage(memberId, memberName, isOnline, chatRoomId);

            // RabbitMQ로 메시지 전송
            sendMessageToRoom(chatRoomId, statusMessage);
        });
    }

    /**
     * 사용자 상태 메시지를 생성하는 메서드.
     * @param memberId 사용자 ID
     * @param memberName 사용자 이름
     * @param isOnline 온라인 여부
     * @param chatRoomId 채팅방 ID
     * @return 상태 메시지 객체
     */
    private ChatMessage createStatusMessage(String memberId, String memberName, boolean isOnline, Long chatRoomId) {
        ChatMessage statusMessage = new ChatMessage();
        statusMessage.setSenderId(memberId); // 사용자 ID 설정
        statusMessage.setRoomId(chatRoomId); // 채팅방 ID 설정
        statusMessage.setContent(memberName + " is " + (isOnline ? "online" : "offline")); // 온라인 상태 메시지 설정
        return statusMessage;
    }
    
    // =======================================================
    // ===================== RabbitMQ 메시지 전송 =====================
    // =======================================================
    
    /**
     * RabbitMQ로 메시지를 전송하는 메서드.
     * @param chatRoomId 채팅방 ID
     * @param message 전송할 메시지
     */
    private void sendMessageToRoom(Long chatRoomId, ChatMessage message) {
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId, message); // 메시지 전송
    }

}