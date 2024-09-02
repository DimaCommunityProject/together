package net.dima_community.CommunityProject.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.dto.AdminNoteDTO;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder

@Entity
@Table(name="ADMINNOTE")
public class AdminNoteEntity {
	@SequenceGenerator(
			name="adminNote_seq"
			, sequenceName="adminNote_seq"
			, initialValue = 1
			, allocationSize = 1
	)
	@Id
	@GeneratedValue(generator="adminNote_seq")
	@Column(name="adminNote_num")
	private Long adminNoteNum;
	
	@Column(name="adminNote_title", nullable=false)
	private String adminNoteTitle;
	
	@Column(name="adminNote_content", nullable=false)
	private String adminNoteContent;
	
	@Column(name="adminNote_hitcount")
	private int adminNoteHitcount;
	
	@Column(name="adminNote_createDate", nullable=false)
	@CreationTimestamp
	private LocalDateTime adminNoteCreateDate;
	
	@Column(name="adminNote_updateDate")
	@LastModifiedDate
	private LocalDateTime adminNoteUpdateDate;
	
	@Column(name="adminNote_originalFileName")
	private String adminNoteOriginalFileName;
	
	@Column(name="adminNote_savedFileName")
	private String adminNoteSavedFileName;
	
	public static AdminNoteEntity toEntity(AdminNoteDTO adminNoteDTO) {
		return AdminNoteEntity.builder()
				.adminNoteNum(adminNoteDTO.getAdminNoteNum())
				.adminNoteTitle(adminNoteDTO.getAdminNoteTitle())
				.adminNoteContent(adminNoteDTO.getAdminNoteContent())
				.adminNoteHitcount(adminNoteDTO.getAdminNoteHitcount())
				.adminNoteOriginalFileName(adminNoteDTO.getAdminNoteOriginalFileName())
				.adminNoteSavedFileName(adminNoteDTO.getAdminNoteSavedFileName())
				.build();
	}
}
