package net.dima_community.CommunityProject.dto.member;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.entity.member.AdminNoteEntity;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class AdminNoteDTO {
	private Long adminNoteNum;
	private String adminNoteTitle;
	private String adminNoteContent;
	private int adminNoteHitcount;
	private LocalDateTime adminNoteCreateDate;
	private LocalDateTime adminNoteUpdateDate;

	// 파일이 첨부되었을 때 추가작업 : DTO가 데이터를 받는 곳이기 때문에 작업해줘야 함
	private MultipartFile uploadFile;
	private String adminNoteOriginalFileName;
	private String adminNoteSavedFileName;

	// 공지사항 리스트 보여줄 내용 추림
	public AdminNoteDTO(Long adminNoteNum, String adminNoteTitle, int adminNoteHitcount,
			LocalDateTime adminNoteCreateDate, String adminNoteOriginalFileName) {
		super();
		this.adminNoteNum = adminNoteNum;
		this.adminNoteTitle = adminNoteTitle;
		this.adminNoteHitcount = adminNoteHitcount;
		this.adminNoteCreateDate = adminNoteCreateDate;
		this.adminNoteOriginalFileName = adminNoteOriginalFileName;
	}

	public static AdminNoteDTO toDTO(AdminNoteEntity adminNoteEntity) {
		return AdminNoteDTO.builder()
				.adminNoteNum(adminNoteEntity.getAdminNoteNum())
				.adminNoteTitle(adminNoteEntity.getAdminNoteTitle())
				.adminNoteContent(adminNoteEntity.getAdminNoteContent())
				.adminNoteHitcount(adminNoteEntity.getAdminNoteHitcount())
				.adminNoteCreateDate(adminNoteEntity.getAdminNoteCreateDate())
				.adminNoteUpdateDate(adminNoteEntity.getAdminNoteUpdateDate())
				.adminNoteOriginalFileName(adminNoteEntity.getAdminNoteOriginalFileName())
				.adminNoteSavedFileName(adminNoteEntity.getAdminNoteSavedFileName())
				.build();
	}

}
