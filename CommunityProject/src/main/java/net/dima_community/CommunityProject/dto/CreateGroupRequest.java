package net.dima_community.CommunityProject.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateGroupRequest {
    private String currentUserId;
    private String roomId;
    private List<String> memberIds;
}
