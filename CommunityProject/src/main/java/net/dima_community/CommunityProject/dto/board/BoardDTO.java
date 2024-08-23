package net.dima_community.CommunityProject.dto.board;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.dto.board.check.BoardCategory;
import net.dima_community.CommunityProject.entity.board.BoardEntity;

@AllArgsConstructor
@NoArgsConstructor
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
    private int hitCount;
    private int likeCount;
    private MultipartFile uploadFile;
    private String originalFileName;
    private String savedFileName;
    private boolean reported;
    
    // Job board specific fields
//    private LocalDateTime deadline;
//    private int limitNumber;
//    private int currentNumber;


    public static BoardDTO toDTO(BoardEntity entity, String memberId){
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
                .reported(entity.isReported())
                .build();
    }

}
