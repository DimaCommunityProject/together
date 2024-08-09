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

    // FK
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
    
    @Column(name = "content", nullable = false)
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

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "saved_file_name")
    private String savedFileName;

    @Column(name = "reported")
    private boolean reported;

    // 자식
    // 1) JobBoardEntity
    @OneToOne(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("board_id")
    private List<JobBoardEntity> jobBoardEntities;
    
    // 2) BoardReport
    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("report_date")
    private List<BoardReportEntity> boardReportEntities;

    // 3) Like



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
                .originalFileName(dto.getOriginalFileName())
                .savedFileName(dto.getSavedFileName())
                .reported(dto.isReported())
                .build();
    }
}
