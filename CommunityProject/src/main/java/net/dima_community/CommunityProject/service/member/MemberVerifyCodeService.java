package net.dima_community.CommunityProject.service.member;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.dto.member.MemberVerifyCodeDTO;
import net.dima_community.CommunityProject.entity.member.MemberVerifyCodeEntity;
import net.dima_community.CommunityProject.repository.member.MemberVerifyCodeRepository;

@Service
@RequiredArgsConstructor
public class MemberVerifyCodeService {
    private final MemberVerifyCodeRepository memberVerifyCodeRepository;

    public void insert(String memberId, String generatedString) {
        MemberVerifyCodeDTO memberVerifyCodeDTO = MemberVerifyCodeDTO.builder()
                .memberId(memberId)
                .verifyCode(generatedString)
                .build();
        memberVerifyCodeRepository.save(MemberVerifyCodeEntity.toEntity(memberVerifyCodeDTO));
    }

    public boolean verifyMemberByCode(String memberId, String code) {
        Optional<MemberVerifyCodeEntity> entity = memberVerifyCodeRepository.findByMemberId(memberId);
        MemberVerifyCodeDTO dto = null;
        if (entity.isPresent()) {
            dto = MemberVerifyCodeDTO.toDTO(entity.get());
        } else {
            throw new ResourceNotFoundException("MemberVerifyCode", memberId);
        }
        if (!code.trim().equals(dto.getVerifyCode()))
            return false;
        else
            return true;
    }

    public void updateVerificationCode(String memberId, String generatedString) {
        Optional<MemberVerifyCodeEntity> entity = memberVerifyCodeRepository.findByMemberId(memberId);
        if (entity.isPresent()) {
            MemberVerifyCodeEntity getEntity = entity.get();
            getEntity.setVerifyCode(generatedString);
            memberVerifyCodeRepository.save(getEntity);
        } else {
            throw new ResourceNotFoundException("MemberVerifyCode", memberId);
        }
    }
}
