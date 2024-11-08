package net.dima_community.CommunityProject.entity.board;

import java.util.List;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.dto.board.BoardDTO;
import net.dima_community.CommunityProject.dto.board.check.BoardCategory;
import net.dima_community.CommunityProject.entity.member.MemberEntity;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Entity
@Table(name="board")
public class BoardEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    // FK (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @Column(name = "member_group", nullable = false)
    private String memberGroup;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardCategory category;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    private String content;
    
    @Column(name = "create_date")
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "hit_count")
    private int hitCount;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "reply_count")
    private int replyCount;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "saved_file_name")
    private String savedFileName;

    @Column(name = "reported")
    private boolean reported;

    // FK (1:1)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_board_id")
    private JobBoardEntity jobBoardEntity;

    // 자식
    // 1) BoardReport
    @OneToOne(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private BoardReportEntity boardReportEntity;
    // 2) Like
    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("member_id")
    private List<LikeEntity> likeEntities;
    // 3) Reply
    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("reply_id")
    private List<ReplyEntity> replyEntity;

    /**
     * Entity 변환 함수 (jobBoardEntity 값은 null로 세팅)
     * @param dto
     * @param memberEntity
     * @return
     */
    public static BoardEntity toEntity(BoardDTO dto, MemberEntity memberEntity){
        return BoardEntity.builder()
                .boardId(dto.getBoardId())
                .memberEntity(memberEntity)
                .memberGroup(dto.getMemberGroup())
                .category(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createDate(dto.getCreateDate())
                .updateDate(dto.getUpdateDate())
                .hitCount(dto.getHitCount())
                .likeCount(dto.getLikeCount())
                .replyCount(dto.getReplyCount())
                .originalFileName(dto.getOriginalFileName())
                .savedFileName(dto.getSavedFileName())
                .reported(dto.isReported())
                .jobBoardEntity(null)
                .build();
    }

}
