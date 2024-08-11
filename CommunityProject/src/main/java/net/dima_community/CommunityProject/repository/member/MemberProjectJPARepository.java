package net.dima_community.CommunityProject.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dima_community.CommunityProject.entity.member.MemberProjectEntity;

public interface MemberProjectJPARepository extends JpaRepository<MemberProjectEntity, Long> {

}
