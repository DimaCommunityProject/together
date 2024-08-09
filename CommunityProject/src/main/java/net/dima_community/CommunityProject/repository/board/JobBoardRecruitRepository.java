package net.dima_community.CommunityProject.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dima_community.CommunityProject.entity.board.JobBoardRecruitEntity;

public interface JobBoardRecruitRepository extends JpaRepository<JobBoardRecruitEntity,Long> {
    
}
