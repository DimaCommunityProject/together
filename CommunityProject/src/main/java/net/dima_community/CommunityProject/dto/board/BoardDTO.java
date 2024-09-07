package net.dima_community.CommunityProject.dto.board;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

<<<<<<< HEAD
=======

>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
<<<<<<< HEAD
import net.dima_community.CommunityProject.entity.board.BoardEntity;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
=======
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
import net.dima_community.CommunityProject.dto.board.check.BoardCategory;
import net.dima_community.CommunityProject.entity.board.BoardEntity;

@AllArgsConstructor
<<<<<<< HEAD
=======
@RequiredArgsConstructor
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
@Setter
@Getter
@ToString
@Builder
public class BoardDTO {
    private Long boardId;
    private String memberId;
    private String memberGroup;
    private BoardCategory category;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
<<<<<<< HEAD
    private Integer hitCount;
    private Integer likeCount;
    private MultipartFile uploadFile;
    private String originalFileName;
    private String savedFileName;
    private Integer reported;
    private Long jobBoardId;

=======
    private int hitCount;
    private int likeCount;
    private int replyCount;
    private MultipartFile uploadFile;
    private String originalFileName;
    private String savedFileName;
    private boolean reported;
    private Long jobBoardId;
    
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
    // Job board specific fields
    private LocalDateTime deadline;
    private int limitNumber;
    private int currentNumber;
<<<<<<< HEAD

    /**
     * jobBoard 정보가 없는 게시글인 경우 DTO변환 함수
     * 
=======
    private int dDay;

    /**
     *  jobBoard 정보가 없는 게시글인 경우 DTO변환 함수
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
     * @param entity
     * @param memberId
     * @return
     */
<<<<<<< HEAD
    public static BoardDTO toDTO(BoardEntity entity, String memberId) {
=======
    public static BoardDTO toDTO(BoardEntity entity, String memberId){
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
        return BoardDTO.builder()
                .boardId(entity.getBoardId())
                .memberId(memberId)
                .memberGroup(entity.getMemberGroup())
                .category(entity.getCategory())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .hitCount(entity.getHitCount())
                .likeCount(entity.getLikeCount())
<<<<<<< HEAD
                .originalFileName(entity.getOriginalFileName())
                .savedFileName(entity.getSavedFileName())
                // .reported(entity.isReported())
                .build();
    }

=======
                .replyCount(entity.getReplyCount())
                .originalFileName(entity.getOriginalFileName())
                .savedFileName(entity.getSavedFileName())
                .reported(entity.isReported())
                .jobBoardId(null)
                .build();
    }

    /**
     * jobBoard 정보가 있는 게시글인 경우 DTO 변환 함수
     * @param entity
     * @param memberId
     * @param jobBoardId
     * @return
     */
    public static BoardDTO toDTO(BoardEntity entity, String memberId, Long jobBoardId){
        return BoardDTO.builder()
                .boardId(entity.getBoardId())
                .memberId(memberId)
                .memberGroup(entity.getMemberGroup())
                .category(entity.getCategory())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .hitCount(entity.getHitCount())
                .likeCount(entity.getLikeCount())
                .replyCount(entity.getReplyCount())
                .originalFileName(entity.getOriginalFileName())
                .savedFileName(entity.getSavedFileName())
                .reported(entity.isReported())
                .jobBoardId(jobBoardId)
                .build();
    }
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
}
