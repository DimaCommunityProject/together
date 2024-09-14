package net.dima_community.CommunityProject.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatusUpdateRequest {
    private String userId;
    private Long roomId;
    private String status;
}