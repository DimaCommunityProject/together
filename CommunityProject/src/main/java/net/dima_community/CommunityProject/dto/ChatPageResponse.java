package net.dima_community.CommunityProject.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatPageResponse {
    private String currentUserId;
    private List<String> chatRooms;
    private List<MemberDTO> members;
}