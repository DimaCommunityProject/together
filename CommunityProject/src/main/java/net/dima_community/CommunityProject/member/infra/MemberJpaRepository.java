package net.dima_community.CommunityProject.member.infra;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, String> {

    Optional<MemberEntity> findByMemberEmail(String to);

}
