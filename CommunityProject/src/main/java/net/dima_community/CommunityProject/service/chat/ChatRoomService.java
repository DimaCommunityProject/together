package net.dima_community.CommunityProject.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
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

    private final Map<Long, Set<String>> roomOnlineUsers = new ConcurrentHashMap<>();

    // ===================== 1:1 채팅방 생성 =====================
    
    
    /**
     * 고유 키로 기존에 채팅방이 존재하는지 확인 
     * @param createdBy
     * @param recipientId
     * @return
     */
    @Transactional
    public ChatRoom createChatRoom(String createdBy, String recipientId) {
        String uniqueKey = Stream.of(createdBy, recipientId).sorted().collect(Collectors.joining(","));

        // 기존 채팅방을 찾기
        Optional<ChatRoom> existingChatRoomOpt = chatRoomRepository.findByUniqueKey(uniqueKey);

        if (existingChatRoomOpt.isPresent()) {
            ChatRoom existingChatRoom = existingChatRoomOpt.get();

            // 멤버 중 deleted 값이 1인 멤버가 있는지 확인
            boolean hasDeletedMember = existingChatRoom.getChattingRoomMembers()
                    .stream()
                    .anyMatch(member -> member.getDeleted() == 1);

            // deleted 값이 1인 멤버가 있다면 새로운 채팅방을 생성
            if (hasDeletedMember) {
                return createNewChatRoom(createdBy, recipientId, uniqueKey);
            } else {
                // 기존 채팅방에 deleted 멤버가 없으면 기존 채팅방 반환
                return existingChatRoom;
            }
        } else {
            // 기존 채팅방이 없으면 새로운 채팅방을 생성
            return createNewChatRoom(createdBy, recipientId, uniqueKey);
        }
    }

    /**
     * 새로운 채팅방 생성 
     * @param createdBy
     * @param recipientId
     * @param uniqueKey
     * @return
     */
    private ChatRoom createNewChatRoom(String createdBy, String recipientId, String uniqueKey) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setCreatedDate(LocalDateTime.now());
        chatRoom.setName(createdBy + "-" + recipientId);
        chatRoom.setCreatedBy(createdBy);
        chatRoom.setUniqueKey(uniqueKey);
        chatRoomRepository.save(chatRoom);

        addMembersToChatRoom(chatRoom.getId(), List.of(createdBy, recipientId));
        createRabbitMQQueue(chatRoom.getId());

        return chatRoom;
    }
    
    /**
     * 큐 생성 
     * @param roomId
     */
    private void createRabbitMQQueue(Long roomId) {
        String queueName = "chat.room." + roomId;
        Queue queue = new Queue(queueName, true);
        rabbitAdmin.declareQueue(queue);
        Binding binding = BindingBuilder.bind(queue).to(new TopicExchange("chat.exchange")).with(queueName);
        rabbitAdmin.declareBinding(binding);
    }

    // ===================== 그룹 채팅방 생성 =====================
    @Transactional
    public ChatRoom createGroupChat(String currentUserId, String existingRoomId, List<String> newMemberIds) {
        Long roomId = Long.parseLong(existingRoomId);
        
        // 기존 멤버와 새로운 멤버 합치기
        List<String> allMemberIds = mergeMemberIds(roomId, newMemberIds);
        
        // 모든 멤버를 정렬하여 고유한 키 생성
        String uniqueKey = String.join("-", allMemberIds);
        
        // 기존 채팅방 확인
        Optional<ChatRoom> existingChatRoomOpt = chatRoomRepository.findByUniqueKey(uniqueKey);
        
        if (existingChatRoomOpt.isPresent()) {
            ChatRoom existingChatRoom = existingChatRoomOpt.get();
            
            // 기존 채팅방에 deleted 값이 1인 멤버가 있는지 확인
            boolean hasDeletedMember = existingChatRoom.getChattingRoomMembers()
                    .stream()
                    .anyMatch(member -> member.getDeleted() == 1);

            // 만약 deleted 멤버가 있으면 새로운 채팅방 생성
            if (hasDeletedMember) {
                return createNewGroupChat(currentUserId, allMemberIds, uniqueKey);
            } else {
                // 삭제된 멤버가 없으면 기존 채팅방으로 접속
                return existingChatRoom;
            }
        }
        
        // 기존 채팅방이 없다면 새로운 채팅방 생성
        return createNewGroupChat(currentUserId, allMemberIds, uniqueKey);
    }

    /**
     * 새로운 그룹 채팅방 생성
     * @param createdBy
     * @param allMemberIds
     * @param uniqueKey
     * @return
     */
    private ChatRoom createNewGroupChat(String createdBy, List<String> allMemberIds, String uniqueKey) {
        ChatRoom newRoom = new ChatRoom();
        newRoom.setName(uniqueKey);
        newRoom.setCreatedDate(LocalDateTime.now());
        newRoom.setCreatedBy(createdBy);
        newRoom.setUniqueKey(uniqueKey);
        chatRoomRepository.save(newRoom);

        addMembersToChatRoom(newRoom.getId(), allMemberIds);
        createRabbitMQQueue(newRoom.getId());

        return newRoom;
    }

    /**
     * 기존 멤버와 새로운 멤버 합치기 
     * @param roomId
     * @param newMemberIds
     * @return
     */
    private List<String> mergeMemberIds(Long roomId, List<String> newMemberIds) {
        List<String> existingMemberIds = chatRoomRepository.findMemberIdsByChatRoomId(roomId);
        Set<String> allMembers = new HashSet<>(existingMemberIds);
        allMembers.addAll(newMemberIds);
        return new ArrayList<>(allMembers);
    }

    /**
     * 특정 채팅방에 멤버 추가 
     * @param chatRoomId
     * @param memberIds
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

    public List<Map<String, Object>> getChatRoomDetails(String userId) {
        List<Long> roomIds = chattingRoomMemberRepository.findByMember_MemberIdAndDeleted(userId, 0)
                .stream().map(member -> member.getChatRoom().getId()).collect(Collectors.toList());

        return chatRoomRepository.findAllById(roomIds)
                .stream()
                .map(this::mapChatRoomToDetails)
                .collect(Collectors.toList());
    }

    private Map<String, Object> mapChatRoomToDetails(ChatRoom chatRoom) {
        Map<String, Object> roomDetails = new HashMap<>();
        roomDetails.put("id", chatRoom.getId());
        roomDetails.put("name", chatRoom.getName());
        roomDetails.put("uniqueKey", chatRoom.getUniqueKey());

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

    /**
	 * MemberDTO를 반환하는 메소드 추가
	 * @param memberId
	 * @return MemberDTO
	 */
    public MemberDTO findByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .map(MemberDTO::toDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + memberId));
    }

    /**
	 *  해당 roomId의 전체 회원 조회 (delted 상태 포함)
	 */
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll().stream().map(MemberDTO::toDTO).collect(Collectors.toList());
    }
    
    /**
     * 채팅방에 사용자 추가
     * @param roomId
     * @param userId
     */
    public void addUserToRoom(Long roomId, String userId) {
        roomOnlineUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    /**
     * 채팅방 나가기
     * 1. delated 값을 1로 변경
     * 2. 유니크 값에서 해당 회원의 아이디 삭제
     * 3. 모든 회원이 나갔을 경우 해당 roomId의 데이터 삭제
     * @param roomId
     * @param userId
     * @return
     */
    @Transactional
    public boolean leaveChatRoom(Long roomId, String userId) {
        return chattingRoomMemberRepository.findByChatRoom_IdAndMember_MemberId(roomId, userId)
                .map(member -> {
                    // deleted를 1로 설정
                    member.setDeleted(1);
                    chattingRoomMemberRepository.save(member);
                    
                    // 해당 채팅방에 active(삭제되지 않은) 멤버가 남아 있는지 확인
                    List<ChattingRoomMemberEntity> activeMembers = chattingRoomMemberRepository.findActiveMembersByChatRoomId(roomId);
                    
                    if (activeMembers.isEmpty()) {
                        // 만약 채팅방에 남은 멤버가 없으면 채팅방 삭제
                        deleteRoomIfAllMembersDeleted(roomId);
                    } else {
                        // 유니크 키에서 나가는 사용자의 ID를 제거
                        ChatRoom chatRoom = member.getChatRoom();
                        updateChatRoomUniqueKey(chatRoom, userId);
                    }
                    return true;
                }).orElse(false);
    }
    
    /**
     * 유니크 키에서 채팅방을 나간 회원의 id 삭제하고 새로운 유니크키 생성 
     * @param chatRoom
     * @param userId
     */
    @Transactional
    public void updateChatRoomUniqueKey(ChatRoom chatRoom, String userId) {
        // 현재 채팅방의 모든 멤버들의 ID를 가져와서 유니크 키를 다시 생성
        List<String> remainingMemberIds = chatRoom.getChattingRoomMembers()
                .stream()
                .filter(member -> !member.getMember().getMemberId().equals(userId))
                .map(member -> member.getMember().getMemberId())
                .sorted() // 멤버 아이디를 정렬
                .collect(Collectors.toList());

        // 새로운 유니크 키 생성
        String newUniqueKey = String.join("-", remainingMemberIds);
        chatRoom.setUniqueKey(newUniqueKey);
        
        // 변경 사항 저장
        chatRoomRepository.save(chatRoom);
        
        log.info("유니크 키가 {}로 업데이트 되었습니다.", newUniqueKey);
    }
    
    /**
     * 모든 회원이 채팅방에 나갔을 때 해당 roomId의 데이터 삭제 
     * @param roomId
     */
    @Transactional
    public void deleteRoomIfAllMembersDeleted(Long roomId) {
        // 1. 해당 채팅방의 모든 멤버 조회 (MySQL)
        List<ChattingRoomMemberEntity> members = chattingRoomMemberRepository.findByChatRoomId(roomId);

        // 2. 모든 멤버의 deleted 상태가 1인지 확인
        boolean allMembersDeleted = members.stream()
                .allMatch(member -> member.getDeleted() == 1);

        if (allMembersDeleted) {
            // 3. MySQL에서 ChatRoom과 ChattingRoomMember 삭제
            chatRoomRepository.deleteById(roomId);
            chattingRoomMemberRepository.deleteAll(members);

            // 4. MongoDB에서 채팅 메시지 삭제
            chatMessageRepository.deleteByRoomId(roomId);

            // 5. RabbitMQ 큐도 삭제 (선택 사항)
            String queueName = "chat.room." + roomId;
            rabbitAdmin.deleteQueue(queueName);

            log.info("채팅방 ID: {} 의 모든 멤버가 deleted 상태이므로 해당 채팅방과 관련된 모든 데이터를 삭제하였습니다.", roomId);
        }
    }

    /**
     * 회원id로 이름 찾기 
     * @param userId
     * @return userName
     */
    public String getUserNameByUserId(String userId) {
        return memberRepository.findByMemberId(userId)
                .map(MemberEntity::getMemberName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    /**
     * 사용자가 현재 해당 방에 속해 있는지 확인 
     * @param roomId
     * @param userId
     * @return
     */
    public boolean isUserInRoom(Long roomId, String userId) {
        return roomOnlineUsers.getOrDefault(roomId, new HashSet<>()).contains(userId);
    }

    /**
     * uniqueKey로 채팅방 찾기
     * @param uniqueKey
     * @return Optional<ChatRoom>
     */
    public Optional<ChatRoom> findChatRoomByUniqueKey(String uniqueKey) {
        return chatRoomRepository.findByUniqueKey(uniqueKey);
    }

}