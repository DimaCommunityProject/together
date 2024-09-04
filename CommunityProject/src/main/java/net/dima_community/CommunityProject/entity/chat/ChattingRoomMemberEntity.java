package net.dima_community.CommunityProject.entity.chat;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dima_community.CommunityProject.entity.MemberEntity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chatting_room_member")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChattingRoomMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_room_member_id")
    private Long id;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "deleted", nullable = false)
    private Integer deleted;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_id", nullable = false)
    @JsonBackReference  // ChatRoom에서의 JsonManagedReference와 쌍을 이루는 어노테이션
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;
}