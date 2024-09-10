package net.dima_community.CommunityProject.controller.chat;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.util.UserStatusManager;
import net.dima_community.CommunityProject.dto.chat.ChatRoomDTO;
import net.dima_community.CommunityProject.dto.chat.ChatRoomMemberDTO;
import net.dima_community.CommunityProject.dto.chat.StatusUpdateRequest;
import net.dima_community.CommunityProject.dto.chat.StatusUpdateResponse;
import net.dima_community.CommunityProject.dto.member.MemberDTO;
import net.dima_community.CommunityProject.entity.chat.ChatMessage;
import net.dima_community.CommunityProject.entity.chat.ChatRoom;
import net.dima_community.CommunityProject.service.chat.ChatRoomService;
import net.dima_community.CommunityProject.service.chat.ChatService;

/**
 * ChatRoomController - 채팅방과 관련된 API 엔드포인트들을 관리하는 컨트롤러 클래스.
 * 채팅방 생성, 그룹 채팅방 생성, 메시지 조회 및 멤버 목록 등을 관리.
 */

@Slf4j
@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final ChatService chatService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // =======================================================
    // ===================== 채팅방 페이지 =====================
    // =======================================================

    /**
     * 채팅 페이지에 필요한 데이터들을 가져오는 메서드.
     * @param model 화면에 데이터를 전달할 모델 객체
     * @param principal 현재 로그인한 사용자의 정보를 포함하는 Principal 객체
     * @return 채팅 페이지 경로
     */
    @GetMapping("/chatPage")
    public String chatPage(Model model, Principal principal) {
        String currentUserId = principal.getName();
        List<Map<String, Object>> chatRooms = chatRoomService.getChatRoomDetails(currentUserId);
        
        // 채팅방이 있으면 첫 번째 채팅방의 uniqueKey를 모델에 추가
        if (!chatRooms.isEmpty()) {
            model.addAttribute("currentUniqueKey", chatRooms.get(0).get("uniqueKey"));
        }
        
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("chatRooms", chatRoomService.getChatRoomDetails(currentUserId));
        return "chat/app-chat";  // 템플릿 경로
    }

    /**
     * 채팅 페이지 데이터를 JSON 형태로 반환하는 API.
     * @param principal 현재 로그인한 사용자의 정보를 포함하는 Principal 객체
     * @return 채팅 페이지에 필요한 데이터 맵
     */
    @GetMapping("/chatData")
    @ResponseBody
    public Map<String, Object> getChatPageData(Principal principal) {
        String currentUserId = principal.getName();
        MemberDTO currentUser = chatRoomService.findByMemberId(currentUserId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("currentUser", currentUser);
        
        // 삭제되지 않은 채팅방 정보와 유니크 키를 포함해 반환
        List<Map<String, Object>> chatRooms = chatRoomService.getChatRoomDetails(currentUserId);
        response.put("chatRooms", chatRooms);
        response.put("members", chatRoomService.getAllMembers());
        
        return response;
    }
    
    

    // =======================================================
    // ===================== 메시지 조회 =====================
    // =======================================================

    /**
     * 채팅방의 메시지를 가져오는 API.
     * @param roomId 채팅방 ID
     * @return 채팅 메시지 리스트
     */
    @GetMapping("/getMessages/{roomId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable("roomId") Long roomId) {
        List<ChatMessage> messages = chatService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }

    // =======================================================
    // ===================== 1:1 채팅방 시작 =====================
    // =======================================================

    /**
     * 새로운 1:1 채팅방을 생성하는 메서드.
     * @param principal 현재 로그인한 사용자의 정보를 포함하는 Principal 객체
     * @param recipientId 상대방의 사용자 ID
     * @return 생성된 채팅방의 정보
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startChatRoom(Principal principal, @RequestParam("recipientId") String recipientId) {
        String currentUserId = principal.getName();
        ChatRoom chatRoom = chatRoomService.createChatRoom(currentUserId, recipientId);
        notifyUsersAboutNewChatRoom(ChatRoomDTO.fromEntity(chatRoom), currentUserId, recipientId);
        return ResponseEntity.ok(createChatRoomResponse(ChatRoomDTO.fromEntity(chatRoom)));
    }

    // =======================================================
    // ===================== 그룹 채팅방 생성 =====================
    // =======================================================

    /**
     * 새로운 그룹 채팅방을 생성하는 메서드.
     * @param request 그룹 채팅방 요청 정보
     * @return 생성된 그룹 채팅방의 ID
     */
    @PostMapping("/createGroup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createGroupChat(@RequestBody ChatRoomDTO request) {
        ChatRoom newRoom = chatRoomService.createGroupChat(request.getCurrentUserId(), request.getRoomId(), request.getMemberIds());
        notifyUsersAboutNewChatRoom(ChatRoomDTO.fromEntity(newRoom), request.getCurrentUserId());
        return ResponseEntity.ok(Map.of("roomId", newRoom.getId()));
    }

    // =======================================================
    // ================ 채팅방 멤버 목록 조회 ==================
    // =======================================================

    /**
     * 특정 채팅방의 멤버 목록과 상태를 반환하는 API.
     * @param uniqueKey 채팅방의 고유 키
     * @return 멤버 목록과 상태
     */
    @GetMapping("/getRoomMemberNamesByUniqueKey/{uniqueKey}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoomMemberNamesByUniqueKey(@PathVariable("uniqueKey") String uniqueKey) {
        try {
            // uniqueKey로 채팅방을 찾음
            Optional<ChatRoom> chatRoomOpt = chatRoomService.findChatRoomByUniqueKey(uniqueKey);
            
            if (chatRoomOpt.isPresent()) {
                ChatRoom chatRoom = chatRoomOpt.get();

                // 멤버들의 상태를 가져옴
                List<ChatRoomMemberDTO> memberDetails = chatRoom.getChattingRoomMembers().stream()
                    .map(member -> {
                        // UserStatusManager를 통해 상태 조회
                        String status = UserStatusManager.getUserStatus(member.getMember().getMemberId());  
                        System.out.println("User: " + member.getMember().getMemberId() + " Status: " + status); // 로그 추가

                        return new ChatRoomMemberDTO(
                            member.getMember().getMemberId(), 
                            member.getMember().getMemberName(), 
                            member.getMember().getMemberGroup(),  // 회원 기수 정보
                            status,  // 온라인/오프라인 상태
                            member.getDeleted()  // 삭제 여부
                        );
                    })
                    .collect(Collectors.toList());

                // 멤버 정보를 포함한 응답 반환
                return ResponseEntity.ok(Map.of("members", memberDetails));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Chat room not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 
     * @param request
     * @return
     */
    @PostMapping("/updateStatus")
    public ResponseEntity<String> updateStatus(@RequestBody StatusUpdateRequest request) {
        String userId = request.getUserId();
        String status = request.getStatus();

        // 사용자 상태 업데이트
        UserStatusManager.updateUserStatus(userId, status);

        // 상태 변경 알림을 같은 채팅방의 다른 사용자들에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chat.room." + request.getRoomId(),
            new StatusUpdateResponse(userId, status));

        return ResponseEntity.ok("Status updated successfully");
    }

    // =======================================================
    // ===================== 채팅방 나가기 =====================
    // =======================================================

    /**
     * 사용자가 채팅방에서 나가는 API.
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 성공 여부 메시지
     */
    @PostMapping("/leaveRoom")
    public ResponseEntity<String> leaveRoom(@RequestParam("roomId") Long roomId, @RequestParam("userId") String userId) {
        if (chatRoomService.leaveChatRoom(roomId, userId)) {
            return ResponseEntity.ok("User left the room successfully.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error leaving the room.");
    }

    // =======================================================
    // ===================== 유틸리티 메소드 =====================
    // =======================================================

    /**
     * 새로운 채팅방이 생성되었을 때 관련 사용자들에게 알림을 보내는 메서드.
     * @param chatRoomDTO 채팅방 DTO
     * @param userIds 알림을 받을 사용자 ID 목록
     */
    private void notifyUsersAboutNewChatRoom(ChatRoomDTO chatRoomDTO, String... userIds) { 
        for (String userId : userIds) {
            String queueName = "/user/" + userId + "/queue/newChatRoom";
            rabbitMessagingTemplate.convertAndSend(queueName, chatRoomDTO);
        }
    }

    /**
     * 채팅방 생성 후 응답 데이터 생성 메서드.
     * @param chatRoomDTO 채팅방 DTO
     * @return 채팅방의 ID와 이름이 담긴 맵
     */
    private Map<String, Object> createChatRoomResponse(ChatRoomDTO chatRoomDTO) {
        return Map.of("id", chatRoomDTO.getRoomId(), "name", chatRoomDTO.getName());
    }
}