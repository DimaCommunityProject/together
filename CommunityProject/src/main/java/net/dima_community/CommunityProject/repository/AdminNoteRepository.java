package net.dima_community.CommunityProject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.dima_community.CommunityProject.entity.AdminNoteEntity;

public interface AdminNoteRepository extends JpaRepository<AdminNoteEntity, Long> {
	
	//공지사항 페이지
	Page<AdminNoteEntity> findAll(Pageable pageable);
}
