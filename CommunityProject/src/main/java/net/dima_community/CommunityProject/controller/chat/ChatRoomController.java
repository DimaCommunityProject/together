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
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.chat.ChatRoomDTO;
import net.dima_community.CommunityProject.dto.chat.ChatRoomMemberDTO;
import net.dima_community.CommunityProject.entity.chat.ChatRoom;
import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;
import net.dima_community.CommunityProject.repository.chat.ChattingRoomMemberRepository;
import net.dima_community.CommunityProject.service.chat.ChatRoomService;
import net.dima_community.CommunityProject.service.member.MemberService;

@Slf4j
@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChattingRoomMemberRepository chattingRoomMemberRepository;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    
    /**
     * 대화상대 추가하기 위해 회원가입한 회원의 목록을 보여줌
     * 추후 검색으로 변경할 예정
     *
     * @param principal
     * @return
     */
    @GetMapping("/chatPage")
    public String chatPage(Model model, Principal principal) {
        String currentUserId = principal.getName();

        // 채팅방 목록 가져오기
        List<Map<String, Object>> roomDetails = chatRoomService.getChatRoomDetails(currentUserId);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("chatRooms", roomDetails);

        // 각 채팅방의 활성화된 멤버 목록 추가
        Map<Long, List<ChattingRoomMemberEntity>> roomMembersMap = new HashMap<>();
        for (Map<String, Object> room : roomDetails) {
            Long roomId = (Long) room.get("roomId");
            List<ChattingRoomMemberEntity> activeMembers = chatRoomService.getActiveMembers(roomId);
            roomMembersMap.put(roomId, activeMembers);
        }

        model.addAttribute("roomMembersMap", roomMembersMap);

        return "chat/app-chat";  // templates/chat/chat.html 파일을 렌더링
    }

    @GetMapping("/chatData")
    @ResponseBody
    public Map<String, Object> getChatPageData(Principal principal) {
        String currentUserId = principal.getName();
        
        // currentUserId를 사용하여 사용자 정보를 가져옵니다.
        MemberDTO currentUser = chatRoomService.findByMemberId(currentUserId);
        String currentUserName = currentUser.getMemberName();  // 사용자 이름을 가져옵니다.
        String currentUserRole = currentUser.getMemberRole(); 
        String currentUserEmail = currentUser.getMemberEmail();
        
        // 채팅방 상세 정보 목록 가져오기
        List<Map<String, Object>> roomDetails = chatRoomService.getChatRoomDetails(currentUserId);
        List<MemberDTO> members = chatRoomService.getAllMembers();

        Map<String, Object> response = new HashMap<>();
        response.put("currentUserId", currentUserId);
        response.put("currentUserName", currentUserName); 
        response.put("currentUserRole", currentUserRole); 
        response.put("currentUserEmail", currentUserEmail);
//        response.put("currentUserIdNum", currentUserIdNum);
        response.put("chatRooms", roomDetails);  // roomDetails를 반환
        response.put("members", members);
        
        return response;
    }

    /**
     * 대화상대 선택 후 채팅하기 버튼을 누르면 채팅방을 생성하는 매서드
     *
     * @param principal
     * @param recipientId
     * @return
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startChatRoom(
            Principal principal,
            @RequestParam("recipientId") String recipientId) {

        // 현재 사용자의 ID를 가져옴
        String currentUserId = principal.getName();

        // 채팅방 생성 또는 기존 채팅방 조회
        ChatRoom chatRoom = chatRoomService.createChatRoom(currentUserId, recipientId);

        // ChatRoom 엔티티를 DTO로 변환
        ChatRoomDTO chatRoomDTO = ChatRoomDTO.fromEntity(chatRoom);

        // 채팅방 정보를 양측 사용자에게 전송
        notifyUsersAboutNewChatRoom(chatRoomDTO, currentUserId, recipientId);

        // 응답 데이터 생성
        Map<String, Object> response = createChatRoomResponse(chatRoomDTO);
        return ResponseEntity.ok(response);
    }
    
    private void notifyUsersAboutNewChatRoom(ChatRoomDTO chatRoomDTO, String... userIds) {
        for (String userId : userIds) {
            String queueName = "/user/" + userId + "/queue/newChatRoom";
            rabbitMessagingTemplate.convertAndSend(queueName, chatRoomDTO);
        }
    }
    
    private Map<String, Object> createChatRoomResponse(ChatRoomDTO chatRoomDTO) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", chatRoomDTO.getRoomId());
        response.put("name", chatRoomDTO.getName());
        return response;
    }
    /**
     * 채팅 멤버 추가하여 그룹채팅 
     *
     * @param chatRoomId
     * @param memberIds
     */
    @PostMapping("/createGroup")
    @ResponseBody
    public Map<String, Object> createGroupChat(@RequestBody ChatRoomDTO request) {
        String currentUserId = request.getCurrentUserId(); // currentUserId를 올바르게 가져옴
        String existingRoomId = request.getRoomId();
        List<String> memberIds = request.getMemberIds();

        // 새로운 방 생성
        ChatRoom newRoom = chatRoomService.createGroupChat(currentUserId, existingRoomId, memberIds);

        // 새로운 채팅방 정보를 모든 참여자에게 알림
        for (String memberId : memberIds) {
            String queueName = "/user/" + memberId + "/queue/newChatRoom";
            rabbitMessagingTemplate.convertAndSend(queueName, newRoom);
        }
        String senderQueue = "/user/" + currentUserId + "/queue/newChatRoom";
        rabbitMessagingTemplate.convertAndSend(senderQueue, newRoom);

        Map<String, Object> response = new HashMap<>();
        response.put("roomId", newRoom.getId());
        return response;
    }
    
    /**
     * 채팅방 나가기 하지 않은 회원 조회 
     * @param roomId
     * @return
     */
    @GetMapping("/chat/rooms/{roomId}/members")
    public ResponseEntity<List<ChattingRoomMemberEntity>> getActiveMembers(@PathVariable Long roomId) {
        List<ChattingRoomMemberEntity> members = chatRoomService.getActiveMembers(roomId);
        return ResponseEntity.ok(members);
    }

    /**
     * 사용자가 참여한 모든 채팅방의 ID와 이름 목록 반환 
     *
     * @param principal
     * @return
     */
    @GetMapping("/getRooms")
    @ResponseBody
    public List<Map<String, Object>> getChatRooms(Principal principal) {
        String currentUserId = principal.getName();
        return chatRoomService.getChatRoomDetails(currentUserId);  // 채팅방의 ID와 이름을 반환하는 메서드
    }
 
    /**
     * 특정 채팅방의 멤버 목록과 상태를 반환
     * @param roomId
     * @return
     */
    @GetMapping("/getRoomMembers/{roomId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoomMembers(@PathVariable("roomId") Long roomId) {
        try {
            List<ChattingRoomMemberEntity> members = chattingRoomMemberRepository.findByChatRoomId(roomId);

            List<ChatRoomMemberDTO> memberDetails = members.stream()
                .map(member -> new ChatRoomMemberDTO(
                    member.getMember().getMemberId(),
                    member.getMember().getMemberName(),
                    chatRoomService.isUserInRoom(roomId, member.getMember().getMemberId()) ? "online" : "offline",
                    member.getDeleted()
                ))
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("members", memberDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
}