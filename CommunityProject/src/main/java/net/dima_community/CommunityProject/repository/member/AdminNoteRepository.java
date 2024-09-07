package net.dima_community.CommunityProject.repository.member;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.dima_community.CommunityProject.entity.member.AdminNoteEntity;

public interface AdminNoteRepository extends JpaRepository<AdminNoteEntity, Long> {

	// 공지사항 페이지
	Page<AdminNoteEntity> findAll(Pageable pageable);

	List<AdminNoteEntity> findTop5ByOrderByAdminNoteCreateDateDesc();
}
