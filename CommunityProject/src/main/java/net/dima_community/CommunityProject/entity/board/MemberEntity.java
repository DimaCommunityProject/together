package net.dima_community.CommunityProject.entity.board;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "member")
public class MemberEntity {
    
    @Id
    @Column(name = "member_id")
    private String memberId;  
    
    @Column(name = "member_group",  nullable = false)
    private String memberGroup;
    
    // 자식
    // 1) Board
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("board_id")
    private List<BoardEntity> jobBoardEntities;
    
    // 2) JobBoardRecruit
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, fetch=FetchType.LAZY, orphanRemoval = true)
    @OrderBy("board_id")
    private List<JobBoardRecruitEntity> jobBoardRecruitEntities;
    
    // 3) Reply
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, fetch=FetchType.LAZY, orphanRemoval = true)
    @OrderBy("create_date")
    private List<ReplyEntity> replyEntities;
    
    // 4) Like
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("board_id")
    private List<LikeEntity> likeEntities;
}
