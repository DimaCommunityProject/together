package net.dima_community.CommunityProject.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.member.MemberDTO;
import net.dima_community.CommunityProject.entity.member.MemberEntity;
import net.dima_community.CommunityProject.entity.chat.ChatMessage;
import net.dima_community.CommunityProject.entity.chat.ChatRoom;
import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;
import net.dima_community.CommunityProject.repository.chat.ChatMessageRepository;
import net.dima_community.CommunityProject.repository.chat.ChatRoomRepository;
import net.dima_community.CommunityProject.repository.chat.ChattingRoomMemberRepository;
import net.dima_community.CommunityProject.repository.member.MemberRepository;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChattingRoomMemberRepository chattingRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RabbitAdmin rabbitAdmin;
    
    private static final String MEMBER_SEPARATOR = ",";
    private static final String QUEUE_PREFIX = "chat.room.";

    private final Map<Long, Set<String>> roomOnlineUsers = new ConcurrentHashMap<>();

    // =======================================================
    // ===================== 유틸리티 메소드 =====================
    // =======================================================
    
    /**
     * 주어진 회원 ID 리스트를 이름으로 변환하여 유니크 키 생성.
     * @param memberIds 회원 ID 리스트
     * @return 이름 기반 유니크 키
     */
    private String generateUniqueKeyFromNames(List<String> memberIds) {
        return memberIds.stream()
                .map(this::getUserNameByUserId) // 이름으로 변환
                .sorted()
                .collect(Collectors.joining(MEMBER_SEPARATOR));
    }

    /**
     * 회원 ID로 해당 회원의 이름을 조회
     * @param memberId 회원 ID
     * @return 회원 이름
     */
    public String getUserNameByUserId(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .map(MemberEntity::getMemberName)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + memberId));
    }
    
    // =======================================================
    // ===================== 채팅방 생성 및 관리 =====================
    // =======================================================
    
    /**
     * 1:1 채팅방 생성
     * @param createdBy 생성자 ID
     * @param recipientId 상대방 ID
     * @return 생성된 또는 기존 채팅방
     */
    @Transactional
    public ChatRoom createChatRoom(String createdBy, String recipientId) {
        List<String> memberIds = List.of(createdBy, recipientId);
        String uniqueKey = generateUniqueKeyFromNames(memberIds);
        return existingChatRoomCheck(createdBy, uniqueKey, memberIds, false);
    }
    
    /**
     * 그룹 채팅방 생성
     * @param currentUserId 생성자 ID
     * @param existingRoomId 기존 채팅방 ID
     * @param newMemberIds 새로 추가된 멤버 ID 리스트
     * @return 생성된 또는 기존 채팅방
     */
    @Transactional
    public ChatRoom createGroupChat(String currentUserId, String existingRoomId, List<String> newMemberIds) {
        Long roomId = Long.parseLong(existingRoomId);
        List<String> allMemberIds = mergeMemberIds(roomId, newMemberIds);
        String uniqueKey = generateUniqueKeyFromNames(allMemberIds);
        return existingChatRoomCheck(currentUserId, uniqueKey, allMemberIds, true);
    }

    /**
     * 고유한 uniqueKey로 기존 채팅방이 존재하는지 확인하고, 필요 시 새로운 채팅방을 생성.
     * @param currentUserId 현재 사용자 ID
     * @param uniqueKey 고유 키
     * @param allMemberIds 모든 멤버 ID 리스트
     * @param isGroupChat 그룹 채팅 여부
     * @return 기존 채팅방 또는 새로운 채팅방
     */
    private ChatRoom existingChatRoomCheck(String currentUserId, String uniqueKey, List<String> allMemberIds, boolean isGroupChat) {
        Optional<ChatRoom> existingChatRoomOpt = chatRoomRepository.findByUniqueKey(uniqueKey);

        if (existingChatRoomOpt.isPresent()) {
            ChatRoom existingChatRoom = existingChatRoomOpt.get();

            // 삭제된 멤버가 있는지 확인
            boolean hasDeletedMember = existingChatRoom.getChattingRoomMembers()
                    .stream().anyMatch(member -> member.getDeleted() == 1);

            // 삭제된 멤버가 있는 경우 새로운 채팅방 생성
            if (hasDeletedMember) {
                return createNewChatRoom(currentUserId, allMemberIds, uniqueKey, isGroupChat);
            } else {
                // 삭제된 멤버가 없으면 기존 채팅방 반환
                return existingChatRoom;
            }
        }
        // 새로운 채팅방 생성 (기존 채팅방이 없을 경우)
        return createNewChatRoom(currentUserId, allMemberIds, uniqueKey, isGroupChat);
    }
    
    /**
     * 새로운 1:1 또는 그룹 채팅방 생성
     * @param createdBy 생성자 ID
     * @param memberIds 멤버 ID 리스트
     * @param uniqueKey 고유 키
     * @param isGroupChat 그룹 채팅 여부
     * @return 새로 생성된 채팅방
     */
    private ChatRoom createNewChatRoom(String createdBy, List<String> memberIds, String uniqueKey, boolean isGroupChat) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(isGroupChat ? uniqueKey : createdBy + "-" + memberIds.get(1));
        chatRoom.setCreatedDate(LocalDateTime.now());
        chatRoom.setCreatedBy(createdBy);
        chatRoom.setUniqueKey(uniqueKey);
        chatRoomRepository.save(chatRoom);

        addMembersToChatRoom(chatRoom.getId(), memberIds);
        createRabbitMQQueue(chatRoom.getId());

        return chatRoom;
    }
    
    // =======================================================
    // ===================== RabbitMQ Queue 생성 =====================
    // =======================================================

    /**
     * RabbitMQ 큐 생성
     * @param roomId 채팅방 ID
     */
    private void createRabbitMQQueue(Long roomId) {
        String queueName = QUEUE_PREFIX + roomId;
        Queue queue = new Queue(queueName, true);
        rabbitAdmin.declareQueue(queue);
        Binding binding = BindingBuilder.bind(queue).to(new TopicExchange("chat.exchange")).with(queueName);
        rabbitAdmin.declareBinding(binding);
    }
    
	 // =======================================================
	 // ===================== 채팅방 관련 유틸리티 =====================
	 // =======================================================
 
    /**
     * 기존 멤버와 새로운 멤버를 합친 리스트 생성
     * @param roomId 채팅방 ID
     * @param newMemberIds 새로운 멤버 ID 리스트
     * @return 모든 멤버 ID 리스트
     */
    private List<String> mergeMemberIds(Long roomId, List<String> newMemberIds) {
        List<String> existingMemberIds = chatRoomRepository.findMemberIdsByChatRoomId(roomId);
        Set<String> allMembers = new HashSet<>(existingMemberIds);
        allMembers.addAll(newMemberIds);
        return new ArrayList<>(allMembers);
    }

    /**
     * 특정 채팅방에 멤버 추가
     * @param chatRoomId 채팅방 ID
     * @param memberIds 멤버 ID 리스트
     */
    @Transactional
    public void addMembersToChatRoom(Long chatRoomId, List<String> memberIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        for (String memberId : memberIds) {
            MemberEntity member = memberRepository.findByMemberId(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));
            ChattingRoomMemberEntity chatMember = new ChattingRoomMemberEntity(chatRoom, member, LocalDateTime.now(), 0);
            chattingRoomMemberRepository.save(chatMember);
        }
    }
    
	 // =======================================================
	 // ================== 채팅방 나가기 및 삭제 ==================
	 // =======================================================
	
	 /**
	  * 사용자가 채팅방에서 나갈 때 호출되는 메서드.
	  * @param roomId 채팅방 ID
	  * @param userId 사용자 ID
	  * @return 사용자의 나가기 동작 성공 여부
	  */
	 @Transactional
	 public boolean leaveChatRoom(Long roomId, String userId) {
	     return chattingRoomMemberRepository.findByChatRoom_IdAndMember_MemberId(roomId, userId)
	             .map(member -> {
	                 // 해당 사용자의 삭제 상태를 설정
	                 member.setDeleted(1);
	                 chattingRoomMemberRepository.save(member);
	
	                 // 남아 있는 활성 멤버들 확인
	                 List<ChattingRoomMemberEntity> activeMembers = chattingRoomMemberRepository.findActiveMembersByChatRoomId(roomId);
	
	                 // 모든 멤버가 나갔다면 채팅방 삭제
	                 if (activeMembers.isEmpty()) {
	                     deleteRoomIfAllMembersDeleted(roomId);
	                 } else {
	                     // 유니크 키에서 해당 사용자를 제거하고 갱신
	                     updateChatRoomUniqueKey(member.getChatRoom(), userId);
	                 }
	                 return true;
	             }).orElse(false);
	 }
	
	 /**
	  * 유니크 키에서 나간 사용자의 이름을 삭제하고 갱신하는 메서드.
	  * @param chatRoom 채팅방 객체
	  * @param userId 나간 사용자 ID
	  */
	 @Transactional
	 public void updateChatRoomUniqueKey(ChatRoom chatRoom, String userId) {
	     // 현재 채팅방의 남아 있는 멤버 이름을 기반으로 새로운 유니크 키 생성
	     List<String> remainingMemberNames = chatRoom.getChattingRoomMembers()
	             .stream()
	             .filter(member -> !member.getMember().getMemberId().equals(userId))
	             .map(member -> member.getMember().getMemberName()) // ID 대신 이름을 가져옴
	             .sorted()  // 이름을 알파벳순으로 정렬
	             .collect(Collectors.toList());

	     // 새로운 유니크 키로 업데이트
	     String newUniqueKey = String.join(MEMBER_SEPARATOR, remainingMemberNames);
	     chatRoom.setUniqueKey(newUniqueKey);
	     chatRoomRepository.save(chatRoom);  // 변경 사항 저장

	     log.info("유니크 키가 {}로 업데이트되었습니다.", newUniqueKey);
	 }
	
	 /**
	  * 모든 멤버가 나갔을 경우 채팅방을 삭제하는 메서드.
	  * @param roomId 채팅방 ID
	  */
	 @Transactional
	 public void deleteRoomIfAllMembersDeleted(Long roomId) {
	     // 1. 해당 채팅방의 모든 멤버 조회
	     List<ChattingRoomMemberEntity> members = chattingRoomMemberRepository.findByChatRoomId(roomId);
	     
	     // 2. 모든 멤버가 삭제 상태인지 확인
	     boolean allMembersDeleted = members.stream().allMatch(member -> member.getDeleted() == 1);
	
	     if (allMembersDeleted) {
	         // 3. MySQL에서 ChatRoom 및 관련 데이터 삭제
	         chatRoomRepository.deleteById(roomId);
	         chattingRoomMemberRepository.deleteAll(members);
	         
	         // 4. MongoDB에서 채팅 메시지 삭제
	         chatMessageRepository.deleteByRoomId(roomId);
	         
	         // 5. RabbitMQ 큐도 삭제
	         rabbitAdmin.deleteQueue(QUEUE_PREFIX + roomId);
	         log.info("채팅방 ID: {} 가 삭제되었습니다.", roomId);
	     }
	 }
	
	 // =======================================================
	 // ================== 채팅방 세부 정보 조회 ==================
	 // =======================================================
	
	 /**
	  * 사용자가 참여 중인 채팅방의 상세 정보를 반환.
	  * @param userId 사용자 ID
	  * @return 채팅방 상세 정보 목록
	  */
	 public List<Map<String, Object>> getChatRoomDetails(String userId) {
	     // 1. 삭제되지 않은 채팅방 ID 목록을 가져옴
	     List<Long> roomIds = chattingRoomMemberRepository.findByMember_MemberIdAndDeleted(userId, 0)
	             .stream().map(member -> member.getChatRoom().getId()).collect(Collectors.toList());
	
	     // 2. 채팅방 ID로 세부 정보 조회
	     return chatRoomRepository.findAllById(roomIds)
	             .stream()
	             .map(this::mapChatRoomToDetails)
	             .collect(Collectors.toList());
	 }
	
	 /**
	  * 채팅방 객체를 맵으로 변환하는 메서드.
	  * @param chatRoom 변환할 채팅방 객체
	  * @return 채팅방 세부 정보가 담긴 맵
	  */
	 private Map<String, Object> mapChatRoomToDetails(ChatRoom chatRoom) {
	     Map<String, Object> roomDetails = new HashMap<>();
	     
	     // 채팅방 ID, 이름, 유니크 키 저장
	     roomDetails.put("id", chatRoom.getId());
	     roomDetails.put("name", chatRoom.getName());
	     roomDetails.put("uniqueKey", chatRoom.getUniqueKey());
	
	     // 채팅방 멤버 정보 변환
	     List<Map<String, Object>> members = chatRoom.getChattingRoomMembers()
	             .stream()
	             .map(member -> {
	                 Map<String, Object> memberMap = new HashMap<>();
	                 memberMap.put("memberId", member.getMember().getMemberId());
	                 memberMap.put("memberName", member.getMember().getMemberName());
	                 memberMap.put("deleted", member.getDeleted());
	                 return memberMap;
	             })
	             .collect(Collectors.toList());
	
	     roomDetails.put("members", members);
	     return roomDetails;
	 }
	
	 // =======================================================
	 // ================== 회원 및 채팅방 관리 ==================
	 // =======================================================
	
	 /**
	  * 주어진 회원 ID로 MemberDTO 반환.
	  * @param memberId 회원 ID
	  * @return MemberDTO 객체
	  */
	 public MemberDTO findByMemberId(String memberId) {
	     return memberRepository.findByMemberId(memberId)
	             .map(MemberDTO::toDTO)
	             .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + memberId));
	 }
	
	 /**
	  * 모든 회원 목록을 반환 (삭제 상태 포함).
	  * @return 회원 목록
	  */
	 public List<MemberDTO> getAllMembers() {
	     return memberRepository.findAll().stream().map(MemberDTO::toDTO).collect(Collectors.toList());
	 }
	
	 /**
	  * 특정 채팅방에 사용자를 추가.
	  * @param roomId 채팅방 ID
	  * @param userId 사용자 ID
	  */
	 public void addUserToRoom(Long roomId, String userId) {
	     roomOnlineUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
	 }
	
	 /**
	  * 주어진 유니크 키로 채팅방을 찾음.
	  * @param uniqueKey 유니크 키
	  * @return Optional<ChatRoom>
	  */
	 public Optional<ChatRoom> findChatRoomByUniqueKey(String uniqueKey) {
	     return chatRoomRepository.findByUniqueKey(uniqueKey);
	 }
	
	 /**
	  * 주어진 방에 사용자가 참여 중인지 확인.
	  * @param roomId 채팅방 ID
	  * @param userId 사용자 ID
	  * @return 참여 여부
	  */
	 public boolean isUserInRoom(Long roomId, String userId) {
	     return roomOnlineUsers.getOrDefault(roomId, new HashSet<>()).contains(userId);
	 }
}