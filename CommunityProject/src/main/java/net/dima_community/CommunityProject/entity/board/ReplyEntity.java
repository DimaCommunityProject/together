package net.dima_community.CommunityProject.entity.board;

import java.time.LocalDateTime;

import org.hibernate.annotations.CurrentTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
<<<<<<< HEAD
=======
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
<<<<<<< HEAD
// import lombok.ToString;
import net.dima_community.CommunityProject.dto.board.ReplyDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
=======
import lombok.ToString;
import net.dima_community.CommunityProject.dto.board.ReplyDTO;
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
<<<<<<< HEAD
// @ToString
=======
@ToString
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
@Builder
@Entity
@Table(name = "reply")
public class ReplyEntity {
    @Id
<<<<<<< HEAD
    @Column(name = "reply_id")
    private Long replyId;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    @Column(name = "parent_reply_id")
    private Long parentReplyId;

    // FK
=======
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reply_id")
    private Long replyId;

    //FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;
    
    @Column(name = "parent_reply_id")
    private Long parentReplyId;
    
    //FK
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "create_date")
    @CurrentTimestamp
    private LocalDateTime createDate;

    @Column(name = "update_time")
    private LocalDateTime updateDate;

    @Column(name = "like_count")
<<<<<<< HEAD
    private Integer likeCount = 0;

    public static ReplyEntity toEntity(ReplyDTO dto, BoardEntity boardEntity, MemberEntity memberEntity) {
        return ReplyEntity.builder()
                .replyId(dto.getReplyId())
                .boardEntity(boardEntity)
                .parentReplyId(dto.getParentReplyId())
                .memberEntity(memberEntity)
                .content(dto.getContent())
                .createDate(dto.getCreateDate())
                .updateDate(dto.getUpdateDate())
                .likeCount(dto.getLikeCount())
                .build();
    }

}
=======
    private int likeCount;

    public static ReplyEntity toEntity(ReplyDTO dto, BoardEntity boardEntity, MemberEntity memberEntity){
        return ReplyEntity.builder()
            .replyId(dto.getReplyId())
            .boardEntity(boardEntity)
            .parentReplyId(dto.getParentReplyId())
            .memberEntity(memberEntity)
            .content(dto.getContent())
            .createDate(dto.getCreateDate())
            .updateDate(dto.getUpdateDate())
            .likeCount(dto.getLikeCount())
            .build();
    }

}
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
