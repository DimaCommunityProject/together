package net.dima_community.CommunityProject.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.ChatPageResponse;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.entity.ChatRoom;
import net.dima_community.CommunityProject.service.ChatRoomService;
import net.dima_community.CommunityProject.service.MemberService;

@Slf4j
@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MemberService memberService;

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

        // 회원 목록 추가
        List<MemberDTO> members = memberService.getAllMembers();
        model.addAttribute("members", members);

        return "chat/chat";  // templates/chat/chat.html 파일을 렌더링
    }

    @GetMapping("/chatData")
    @ResponseBody
    public Map<String, Object> getChatPageData(Principal principal) {
        String currentUserId = principal.getName();
        
        // currentUserId를 사용하여 사용자 정보를 가져옵니다.
        MemberDTO currentUser = memberService.findByMemberId(currentUserId);
        String currentUserName = currentUser.getMemberName();  // 사용자 이름을 가져옵니다.

        // 채팅방 상세 정보 목록 가져오기
        List<Map<String, Object>> roomDetails = chatRoomService.getChatRoomDetails(currentUserId);
        List<MemberDTO> members = memberService.getAllMembers();

        Map<String, Object> response = new HashMap<>();
        response.put("currentUserId", currentUserId);
        response.put("currentUserName", currentUserName); 
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
    public ResponseEntity<Map<String, Object>> startChatRoom(Principal principal, @RequestParam("recipientId") String recipientId) {
        String currentUserId = principal.getName();
        ChatRoom chatRoom = chatRoomService.createChatRoom(currentUserId, recipientId);

        // 로그로 확인
        log.info("Created Chat Room ID: {}", chatRoom.getId());
        log.info("Created Chat Room Name: {}", chatRoom.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("id", chatRoom.getId());
        response.put("name", chatRoom.getName());

        return ResponseEntity.ok(response);
    }

    /**
     * 채팅 멤버 추가
     *
     * @param chatRoomId
     * @param memberIds
     */
    @PostMapping("/addMembers")
    public void addMembersToRoom(@RequestParam Long chatRoomId, @RequestParam List<String> memberIds) {
        chatRoomService.addMemberToChatRoom(chatRoomId, memberIds);
    }

    /**
     * 사용자가 참여한 모든 채팅방의 ID와 이름 목록 반환 
     *
     * @param principal
     * @return
     */
    @GetMapping("/api/chat/getRooms")
    @ResponseBody 
    public List<Map<String, Object>> getChatRooms(Principal principal) {
        String currentUserId = principal.getName();
        return chatRoomService.getChatRoomDetails(currentUserId);  // 채팅방의 ID와 이름을 반환하는 메서드
    }
    
    /**
     * 특정 roomId에 해당하는 채팅방 하나의 정보를 반환 
     * @param roomId
     * @return
     */
    @GetMapping("/chat/room/{roomId}")
    public ResponseEntity<ChatRoom> getChatRoom(@PathVariable("roomId") Long roomId) {
        Optional<ChatRoom> chatRoomOptional = chatRoomService.findById(roomId);
        if (chatRoomOptional.isPresent()) {
            return ResponseEntity.ok(chatRoomOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}