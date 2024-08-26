package net.dima_community.CommunityProject.dto.board;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.entity.board.BoardEntity;

@AllArgsConstructor
@RequiredArgsConstructor
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
    private Integer hitCount;
    private Integer likeCount;
    private MultipartFile uploadFile;
    private String originalFileName;
    private String savedFileName;
    private Integer reported;
    private Long jobBoardId;

    // Job board specific fields
    private LocalDateTime deadline;
    private int limitNumber;
    private int currentNumber;

    /**
     * jobBoard 정보가 없는 게시글인 경우 DTO변환 함수
     * 
     * @param entity
     * @param memberId
     * @return
     */
    public static BoardDTO toDTO(BoardEntity entity, String memberId) {
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
                .originalFileName(entity.getOriginalFileName())
                .savedFileName(entity.getSavedFileName())
                // .reported(entity.isReported())
                .jobBoardId(null)
                .build();
    }

    /**
     * jobBoard 정보가 있는 게시글인 경우 DTO 변환 함수
     * 
     * @param entity
     * @param memberId
     * @param jobBoardId
     * @return
     */
    public static BoardDTO toDTO(BoardEntity entity, String memberId, Long jobBoardId) {
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
                .originalFileName(entity.getOriginalFileName())
                .savedFileName(entity.getSavedFileName())
                // .reported(entity.isReported())
                .jobBoardId(jobBoardId)
                .build();
    }
}