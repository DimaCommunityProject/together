package net.dima_community.CommunityProject.controller.chat;

import java.security.Principal;
import java.time.Instant;
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
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.chat.ChatRoomDTO;
import net.dima_community.CommunityProject.dto.chat.ChatRoomMemberDTO;
import net.dima_community.CommunityProject.entity.chat.ChatMessage;
import net.dima_community.CommunityProject.entity.chat.ChatRoom;
import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;
import net.dima_community.CommunityProject.repository.chat.ChattingRoomMemberRepository;
import net.dima_community.CommunityProject.service.chat.ChatRoomService;
import net.dima_community.CommunityProject.service.chat.ChatService;

@Slf4j
@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChattingRoomMemberRepository chattingRoomMemberRepository;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final ChatService chatService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @GetMapping("/chatPage")
    public String chatPage(Model model, Principal principal) {
        String currentUserId = principal.getName();
        List<Map<String, Object>> chatRooms = chatRoomService.getChatRoomDetails(currentUserId);
        if (!chatRooms.isEmpty()) {
            // 첫 번째 채팅방의 uniqueKey를 모델에 추가
            model.addAttribute("currentUniqueKey", chatRooms.get(0).get("uniqueKey"));
        }
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("chatRooms", chatRoomService.getChatRoomDetails(currentUserId));
        return "chat/app-chat";
    }

    @GetMapping("/chatData")
    @ResponseBody
    public Map<String, Object> getChatPageData(Principal principal) {
        String currentUserId = principal.getName();
        MemberDTO currentUser = chatRoomService.findByMemberId(currentUserId);
        Map<String, Object> response = new HashMap<>();
        response.put("currentUser", currentUser);
        response.put("chatRooms", chatRoomService.getChatRoomDetails(currentUserId));
        response.put("members", chatRoomService.getAllMembers());
        return response;
    }
    
    @GetMapping("/getMessages/{roomId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable("roomId") Long roomId) {
        List<ChatMessage> messages = chatService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startChatRoom(Principal principal, @RequestParam("recipientId") String recipientId) {
        String currentUserId = principal.getName();
        ChatRoom chatRoom = chatRoomService.createChatRoom(currentUserId, recipientId);
        notifyUsersAboutNewChatRoom(ChatRoomDTO.fromEntity(chatRoom), currentUserId, recipientId);
        return ResponseEntity.ok(createChatRoomResponse(ChatRoomDTO.fromEntity(chatRoom)));
    }

    @PostMapping("/createGroup")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createGroupChat(@RequestBody ChatRoomDTO request) {
        ChatRoom newRoom = chatRoomService.createGroupChat(request.getCurrentUserId(), request.getRoomId(), request.getMemberIds());
        notifyUsersAboutNewChatRoom(ChatRoomDTO.fromEntity(newRoom), request.getCurrentUserId());
        return ResponseEntity.ok(Map.of("roomId", newRoom.getId()));
    }

    /**
     * 특정 채팅방의 멤버 목록과 상태 반환 
     * @param uniqueKey
     * @return
     */
    @GetMapping("/getRoomMemberNamesByUniqueKey/{uniqueKey}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoomMemberNamesByUniqueKey(@PathVariable("uniqueKey") String uniqueKey) {
        try {
            // uniqueKey로 채팅방을 찾음
            Optional<ChatRoom> chatRoomOpt = chatRoomService.findChatRoomByUniqueKey(uniqueKey);
            
            if (chatRoomOpt.isPresent()) {
                ChatRoom chatRoom = chatRoomOpt.get();

                // 남아 있는 멤버들의 ID로 멤버 이름을 찾음
                List<ChatRoomMemberDTO> memberDetails = chatRoom.getChattingRoomMembers().stream()
                    .map(member -> new ChatRoomMemberDTO(
                        member.getMember().getMemberId(), 
                        member.getMember().getMemberName(), 
                        "offline", 
                        member.getDeleted()
                    ))
                    .collect(Collectors.toList());

                return ResponseEntity.ok(Map.of("members", memberDetails));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Chat room not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/leaveRoom")
    public ResponseEntity<String> leaveRoom(@RequestParam("roomId") Long roomId, @RequestParam("userId") String userId) {
        if (chatRoomService.leaveChatRoom(roomId, userId)) {
            return ResponseEntity.ok("User left the room successfully.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error leaving the room.");
    }

    private void notifyUsersAboutNewChatRoom(ChatRoomDTO chatRoomDTO, String... userIds) {
        for (String userId : userIds) {
            String queueName = "/user/" + userId + "/queue/newChatRoom";
            rabbitMessagingTemplate.convertAndSend(queueName, chatRoomDTO);
        }
    }

    private Map<String, Object> createChatRoomResponse(ChatRoomDTO chatRoomDTO) {
        return Map.of("id", chatRoomDTO.getRoomId(), "name", chatRoomDTO.getName());
    }
}