package net.dima_community.CommunityProject.entity.chat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_room_id", nullable = false)
    private Long id; 

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "unique_key", nullable = false)
    private String uniqueKey;
    
    @ElementCollection
    @CollectionTable(name = "chat_room_members", joinColumns = @JoinColumn(name = "chat_room_id"))
    @Column(name="member_id")
    private List<String> memberIds;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ChattingRoomMemberEntity> chattingRoomMembers;
}