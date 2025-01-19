package net.dima_community.CommunityProject.dto.chat;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS) 
public class ChatRoomMemberDTO {
    private String memberId;   // 회원 ID
    private String name;       // 회원 이름
    private String status;     // 상태 (온라인/오프라인)
    private Integer deleted;   // 삭제 여부
    private String uniqueKey;  // 유니크 키 (추가)
    private String memberGroup; // 회원기수 
    
    // 생성자
    public ChatRoomMemberDTO(String memberId, String name, String memberGroup, String status, Integer deleted) {
        this.memberId = memberId;
        this.name = name;
        this.memberGroup = memberGroup;
        this.status = status;
        this.deleted = deleted;
    }
}