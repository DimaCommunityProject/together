package net.dima_community.CommunityProject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Document(collection = "chat_app")  //mongo DB
public class ChatMessage {

    @Id
    private String id;
    private String senderId;
    private String content;
    private String timestamp;
    private boolean deleted;
    private String roomId;
    
//      public enum MessageType {
//      CHAT, JOIN, LEAVE
//  }
    
 //test하기 위해  새로운 생성자 추가
    public ChatMessage(String roomId, String content) {
        this.roomId = roomId;
        this.content = content;
        this.timestamp = LocalDateTime.now().toString();
        this.deleted = false;
    }
}



