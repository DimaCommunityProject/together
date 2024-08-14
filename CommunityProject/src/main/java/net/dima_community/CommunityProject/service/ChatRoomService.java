package net.dima_community.CommunityProject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.ChatRoom;
import net.dima_community.CommunityProject.entity.ChattingRoomMemberEntity;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.repository.jpa.ChatRoomRepository;
import net.dima_community.CommunityProject.repository.jpa.ChattingRoomMemberRepository;
import net.dima_community.CommunityProject.repository.jpa.MemberRepository;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
	
	 @Autowired
    private final ChatRoomRepository chatRoomRepository;
    @Autowired
    private final ChattingRoomMemberRepository chattingRoomMemberRepository;
    private final MemberRepository memberRepository;

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

                return savedChatRoom;
            });
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
    
    public List<Map<String, Object>> getChatRoomDetails(String userId) {
        // chatting_room_member 테이블에서 사용자의 chatting_room_id 목록을 가져옴
        List<Long> roomIds = chattingRoomMemberRepository.findByMember_MemberId(userId)
                                                         .stream()
                                                         .map(member -> member.getChatRoom().getId())
                                                         .collect(Collectors.toList());

        // chat_rooms 테이블에서 해당하는 채팅방의 이름을 가져옴
        return roomIds.stream()
                      .map(roomId -> {
                          Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
                          if (chatRoomOpt.isPresent()) {
                              ChatRoom chatRoom = chatRoomOpt.get();
                              Map<String, Object> roomDetails = new HashMap<>(); // 명시적으로 HashMap을 사용
                              roomDetails.put("id", roomId);
                              roomDetails.put("name", chatRoom.getName());
                              return roomDetails;
                          }
                          return null;
                      })
                      .filter(room -> room != null)
                      .collect(Collectors.toList());
    }

    public List<String> getChatRoomNames(String userId) {
        // MySQL에서 멤버와 연결된 방을 조회
        return chattingRoomMemberRepository.findByMember_MemberId(userId)
                .stream()
                .map(memberChattingRoom -> memberChattingRoom.getChatRoom().getName()) // getChatRoomId 대신 getChatRoom().getName() 사용
                .collect(Collectors.toList());
    }
    
    public Optional<ChatRoom> findById(Long roomId) {
        return chatRoomRepository.findById(roomId);
    }
}