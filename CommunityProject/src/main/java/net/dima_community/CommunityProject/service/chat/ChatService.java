package net.dima_community.CommunityProject.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.entity.chat.ChatMessage;
import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;
import net.dima_community.CommunityProject.repository.chat.ChatMessageRepository;
import net.dima_community.CommunityProject.repository.chat.ChattingRoomMemberRepository;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
	
	private static final String CHAT_EXCHANGE_NAME = "chat.exchange";

    private final ChatMessageRepository chatMessageRepository;
    private final ChattingRoomMemberRepository chattingRoomMemberRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, Boolean> userStatusMap = new ConcurrentHashMap<>();

    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }

    public void saveMessage(ChatMessage message, Long roomId) {
        message.setRoomId(roomId); // roomId 설정
        message.setTimestamp(LocalDateTime.now().toString());
        chatMessageRepository.save(message);
        log.info("Message saved to DB: {}", message);
    }

    public void setUserOnlineStatus(String userId, boolean isOnline) {
        userStatusMap.put(userId, isOnline);
    }

    public boolean isUserOnline(String userId) {
        return userStatusMap.getOrDefault(userId, false);
    }
    
    /**
     * 특정 채팅방에 있는 사용자들에게 현재 사용자 상태 알림
     * @param chatRoomId
     */
    @Transactional
    public void notifyUsersInRoom(Long chatRoomId) {
        List<ChattingRoomMemberEntity> members = chattingRoomMemberRepository.findByChatRoomId(chatRoomId);

        members.forEach(member -> {
            MemberEntity memberEntity = member.getMember();
            String memberId = memberEntity.getMemberId();
            String memberName = memberEntity.getMemberName();
            boolean isOnline = isUserOnline(memberId);

            ChatMessage statusMessage = new ChatMessage();
            statusMessage.setSenderId(memberId);
            statusMessage.setRoomId(chatRoomId);
            statusMessage.setContent(memberName + " is " + (isOnline ? "online" : "offline"));

//            rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId, statusMessage);
        });
    }

//    public void sendMessage(String exchange, String routingKey, ChatMessage message, Long roomId) {
//    	message.setRoomId(roomId); 
//    	rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
//            msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
//            return msg;
//        });
//    	saveMessage(message, roomId);
//    }
    
}