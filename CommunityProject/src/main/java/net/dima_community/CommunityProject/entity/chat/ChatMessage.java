package net.dima_community.CommunityProject.entity.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String senderName; 
    private String content;
    private String timestamp;
    private int deleted;
    private Long roomId;

}