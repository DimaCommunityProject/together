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

	 	private String memberId;
	    private String name;
	    private String status;
	    private Integer deleted;
}
