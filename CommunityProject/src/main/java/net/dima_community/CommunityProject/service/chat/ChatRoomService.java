package net.dima_community.CommunityProject.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.entity.chat.ChatRoom;
import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;
import net.dima_community.CommunityProject.repository.chat.ChatRoomRepository;
import net.dima_community.CommunityProject.repository.chat.ChattingRoomMemberRepository;
import net.dima_community.CommunityProject.repository.member.MemberRepository;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.amqp.core.Queue;
import org.hibernate.Hibernate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;


import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
	
	private final ChatRoomRepository chatRoomRepository;
    private final ChattingRoomMemberRepository chattingRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final RabbitAdmin rabbitAdmin;
    
    // 각 방마다 온라인 유저를 관리하는 Map
    private final Map<Long, Set<String>> roomOnlineUsers = new ConcurrentHashMap<>();


    @Transactional
    public ChatRoom createChatRoom(String createdBy, String recipientId) {
    	String uniqueKey = Stream.of(createdBy, recipientId).sorted().collect(Collectors.joining("-"));

        return chatRoomRepository.findByUniqueKey(uniqueKey)
            .orElseGet(() -> {
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
            });
    }
    
    @Transactional
    public ChatRoom createGroupChat(String currentUserId, String existingRoomId, List<String> newMemberIds) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("currentUserId cannot be null");
        }

        try {
            // 기존 채팅방의 멤버 가져오기
            Long roomId = Long.parseLong(existingRoomId);
            List<String> existingMemberIds = chatRoomRepository.findMemberIdsByChatRoomId(roomId);

            // 새로운 멤버들과 기존 멤버들을 합침
            List<String> allMemberIds = new ArrayList<>(existingMemberIds);
            allMemberIds.addAll(newMemberIds);
            allMemberIds = allMemberIds.stream().distinct().collect(Collectors.toList()); // 중복 제거

            // 멤버들을 정렬하고 uniqueKey 생성
            allMemberIds.sort(String::compareTo);
            String uniqueKey = String.join("-", allMemberIds);

            // 새로운 방 생성
            ChatRoom newRoom = new ChatRoom();
            newRoom.setName(uniqueKey); // 멤버들로 이름 생성
            newRoom.setCreatedDate(LocalDateTime.now());
            newRoom.setCreatedBy(currentUserId);
            newRoom.setUniqueKey(uniqueKey);

            // 채팅방 저장
            ChatRoom savedChatRoom = chatRoomRepository.save(newRoom);

            // 모든 멤버 추가
            addMemberToChatRoom(savedChatRoom.getId(), allMemberIds);

            // 큐 이름 생성 및 바인딩
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
    public void addMemberToChatRoom(Long chatRoomId, List<String> memberIds) { // chatRoomId의 타입을 Long으로 변경
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        for (String memberId : memberIds) {
            MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

            ChattingRoomMemberEntity memberChattingRoom = new ChattingRoomMemberEntity();
            memberChattingRoom.setChatRoom(chatRoom); // chatRoomId 대신 chatRoom 객체를 설정
            memberChattingRoom.setMember(member);
            memberChattingRoom.setCreatedDate(LocalDateTime.now());
            memberChattingRoom.setDeleted(false);
            chattingRoomMemberRepository.save(memberChattingRoom);
        }
    }
   
    /**
     * 특정 사용자가 참여하고 있는 채팅방의 id, name 조회 
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getChatRoomDetails(String userId) {
        List<Long> roomIds = chattingRoomMemberRepository.findByMember_MemberId(userId)
                                                          .stream()
                                                          .map(member -> member.getChatRoom().getId())
                                                          .collect(Collectors.toList());

        return chatRoomRepository.findAllById(roomIds)
            .stream()
            .map(chatRoom -> {
                Map<String, Object> roomDetails = new HashMap<>();
                roomDetails.put("id", chatRoom.getId());
                roomDetails.put("name", chatRoom.getName());
                return roomDetails;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 채팅방 ID를 통해 채팅방을 가져오는 메서드
     *
     * @param chatRoomId
     * @return
     */
    public ChatRoom getChatRoomById(Long roomId) {
        try {
            return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("Chat room not found"));
        } catch (NoSuchElementException e) {
            log.error("Failed to retrieve chat room ID: " + roomId, e);
            throw e;  // 혹은 적절한 방식으로 예외를 처리
        }
    }
    
    /**
     * 특정 채팅방에 속한 회원 조회 
     * @param chatRoomId
     * @return
     */
    public List<String> getParticipantIdsByRoomId(Long chatRoomId) {
        return chattingRoomMemberRepository.findByChatRoomId(chatRoomId)
            .stream()
            .map(member -> member.getMember().getMemberId())
            .collect(Collectors.toList());
    }
    
    /**
     * 채팅방에 접속한 멤버의 접속 상태 조회 
     * @param roomId
     * @param userId
     */
    public void addUserToRoom(Long roomId, String userId) {
        roomOnlineUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    public void removeUserFromRoom(Long roomId, String userId) {
        Set<String> onlineUsers = roomOnlineUsers.get(roomId);
        if (onlineUsers != null) {
            onlineUsers.remove(userId);
            if (onlineUsers.isEmpty()) {
                roomOnlineUsers.remove(roomId);
            }
        }
    }

    public boolean isUserInRoom(Long roomId, String userId) {
        return roomOnlineUsers.getOrDefault(roomId, ConcurrentHashMap.newKeySet()).contains(userId);
    }

    public Set<String> getUsersInRoom(Long roomId) {
        return roomOnlineUsers.getOrDefault(roomId, ConcurrentHashMap.newKeySet());
    }
    
    
    
    
   
    
}