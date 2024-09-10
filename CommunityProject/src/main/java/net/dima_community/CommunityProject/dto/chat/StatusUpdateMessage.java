package net.dima_community.CommunityProject.dto.chat;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatusUpdateMessage {
    private String userId;
    private String status;  // "online" 또는 "offline"
    private Long chatRoomId;
}
