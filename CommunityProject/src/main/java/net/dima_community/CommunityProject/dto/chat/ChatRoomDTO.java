package net.dima_community.CommunityProject.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dima_community.CommunityProject.entity.chat.ChatRoom;
import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatRoomDTO {
    private String roomId;   // 채팅방 ID를 문자열로 변환해서 저장
    private String name;     // 채팅방 이름
    private String createdBy; // 생성자 ID
    private LocalDateTime createdDate;  // 생성 날짜
    private Integer deleted;
    private LocalDateTime deletedDate;
    private LocalDateTime lastModifiedDate;
    private String uniqueKey;
    private List<String> memberIds; // 멤버들의 ID를 담는 리스트
    private String currentUserId;   // 현재 사용자의 ID를 추가

    // Static method to convert a ChatRoom entity to a ChatRoomDTO
    public static ChatRoomDTO fromEntity(ChatRoom chatRoom) {
        return new ChatRoomDTO(
            chatRoom.getId().toString(),
            chatRoom.getName(),
            chatRoom.getCreatedBy(),
            chatRoom.getCreatedDate(),
            chatRoom.getDeleted(),
            chatRoom.getDeletedDate(),
            chatRoom.getLastModifiedDate(),
            chatRoom.getUniqueKey(),
            chatRoom.getChattingRoomMembers() != null ?
                chatRoom.getChattingRoomMembers().stream()
                    .map(member -> member.getMember().getMemberId())
                    .collect(Collectors.toList()) : new ArrayList<>(),
            null // currentUserId는 fromEntity에서 설정되지 않음
        );
    }
}