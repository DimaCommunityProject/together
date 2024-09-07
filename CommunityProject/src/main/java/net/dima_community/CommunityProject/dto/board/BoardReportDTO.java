package net.dima_community.CommunityProject.dto.board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
<<<<<<< HEAD
import lombok.NoArgsConstructor;
=======
import lombok.RequiredArgsConstructor;
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.dto.board.check.ReportCategory;
import net.dima_community.CommunityProject.entity.board.BoardReportEntity;

@AllArgsConstructor
<<<<<<< HEAD
@NoArgsConstructor
=======
@RequiredArgsConstructor
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
@Setter
@Getter
@ToString
@Builder
public class BoardReportDTO {
<<<<<<< HEAD
	private Long reportId;
=======
    private Long reportId;
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
    private Long boardId;
    private String memberId;
    private ReportCategory category;
    private String reason;
    private LocalDateTime reportDate;
<<<<<<< HEAD
    private BoardDTO boardDTO;
=======
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc

    public static BoardReportDTO toDTO (BoardReportEntity entity, Long boardId){
        return BoardReportDTO.builder()
            .reportId(entity.getReportId())
            .boardId(boardId)
            .memberId(entity.getMemberId())
            .category(entity.getCategory())
            .reason(entity.getReason())
            .reportDate(entity.getReportDate())
            .build();
    }
<<<<<<< HEAD
    
    //공지사항 리스트 보여줄 내용 추림
	public BoardReportDTO(Long reportId, String reason, ReportCategory category, LocalDateTime reportDate) {
		super();
		this.reportId = reportId;
		this.reason = reason;
		this.category = category;
		this.reportDate = reportDate;
	}
	
	// 공지사항 디테일 내용 추림
	public BoardReportDTO(Long reportId, String reason, ReportCategory category, LocalDateTime reportDate, BoardDTO boardDTO) {
		super();
        this.reportId = reportId;
        this.reason = reason;
        this.category = category;
        this.reportDate = reportDate;
        this.boardDTO = boardDTO;
    }
  
    
    
=======
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
}
