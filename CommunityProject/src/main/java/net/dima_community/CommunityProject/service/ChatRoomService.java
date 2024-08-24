package net.dima_community.CommunityProject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.ChatRoom;
import net.dima_community.CommunityProject.entity.ChattingRoomMemberEntity;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.repository.jpa.ChatRoomRepository;
import net.dima_community.CommunityProject.repository.jpa.ChattingRoomMemberRepository;
import net.dima_community.CommunityProject.repository.jpa.MemberRepository;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
	
    private final ChatRoomRepository chatRoomRepository;
    private final ChattingRoomMemberRepository chattingRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final RabbitAdmin rabbitAdmin;

    @Transactional
    public ChatRoom createChatRoom(String createdBy, String recipientId) {
    	String uniqueKey = Stream.of(createdBy, recipientId).sorted().collect(Collectors.joining("-"));

        List<ChatRoom> existingRooms = chatRoomRepository.findByUniqueKey(uniqueKey);

        if (!existingRooms.isEmpty()) {
            return existingRooms.get(0); // 첫 번째 방을 반환
        }

        // 새로운 채팅방 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setCreatedDate(LocalDateTime.now());
        chatRoom.setDeleted(false);
        chatRoom.setName(createdBy + "-" + recipientId);
        chatRoom.setCreatedBy(createdBy);
        chatRoom.setUniqueKey(uniqueKey);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        addMemberToChatRoom(savedChatRoom.getId(), List.of(createdBy, recipientId));

        // 큐 이름 생성
        String queueName = "chat.room." + savedChatRoom.getId();

        // 큐 생성
        Queue queue = new Queue(queueName, true); // 내구성 있는 큐 생성
        rabbitAdmin.declareQueue(queue);

        // 큐를 Exchange에 바인딩
        Binding binding = BindingBuilder.bind(queue).to(new TopicExchange("chat.exchange")).with(queueName);
        rabbitAdmin.declareBinding(binding);

        return savedChatRoom;
    }
    
    @Transactional
    public ChatRoom createGroupChat(String currentUserId, String existingRoomId, List<String> newMemberIds) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("currentUserId cannot be null");
        }

        try {
            Long roomId = Long.parseLong(existingRoomId);
            List<String> existingMemberIds = chatRoomRepository.findMemberIdsByChatRoomId(roomId);

            List<String> allMemberIds = new ArrayList<>(existingMemberIds);
            allMemberIds.addAll(newMemberIds);
            allMemberIds = allMemberIds.stream().distinct().collect(Collectors.toList());

            allMemberIds.sort(String::compareTo);
            String uniqueKey = String.join("-", allMemberIds);

            List<ChatRoom> existingRooms = chatRoomRepository.findByUniqueKey(uniqueKey);

            if (!existingRooms.isEmpty()) {
                return existingRooms.get(0); // 기존 방이 있을 경우 그 방을 반환
            }

            // 새로운 방 생성
            ChatRoom newRoom = new ChatRoom();
            newRoom.setName(uniqueKey);
            newRoom.setCreatedDate(LocalDateTime.now());
            newRoom.setCreatedBy(currentUserId);
            newRoom.setUniqueKey(uniqueKey);

            ChatRoom savedChatRoom = chatRoomRepository.save(newRoom);

            addMemberToChatRoom(savedChatRoom.getId(), allMemberIds);

            String queueName = "chat.room." + savedChatRoom.getId();
            Queue queue = new Queue(queueName, true);
            rabbitAdmin.declareQueue(queue);
            Binding binding = BindingBuilder.bind(queue).to(new TopicExchange("chat.exchange")).with(queueName);
            rabbitAdmin.declareBinding(binding);

            return savedChatRoom;
        } catch (Exception e) {
            log.error("Error creating group chat", e);
            throw e;
        }
    }

    @Transactional
    public void addMemberToChatRoom(Long chatRoomId, List<String> memberIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        for (String memberId : memberIds) {
            MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

            ChattingRoomMemberEntity memberChattingRoom = new ChattingRoomMemberEntity();
            memberChattingRoom.setChatRoom(chatRoom);
            memberChattingRoom.setMember(member);
            memberChattingRoom.setCreatedDate(LocalDateTime.now());
            memberChattingRoom.setDeleted(false);
            chattingRoomMemberRepository.save(memberChattingRoom);
        }
    }
}