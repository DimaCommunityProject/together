package net.dima_community.CommunityProject.service.member;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.dto.member.MemberVerifyCodeDTO;
import net.dima_community.CommunityProject.entity.member.MemberVerifyCodeEntity;
import net.dima_community.CommunityProject.repository.member.MemberVerifyCodeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberVerifyCodeService {
    private final MemberVerifyCodeRepository memberVerifyCodeRepository;

    public void insert(String memberId, String generatedString) {
        Optional<MemberVerifyCodeEntity> result = memberVerifyCodeRepository.findByMemberId(memberId);
        if (result.isPresent()) {
            updateVerificationCode(result.get(), generatedString);
        } else {
            MemberVerifyCodeDTO memberVerifyCodeDTO = MemberVerifyCodeDTO.builder()
                    .memberId(memberId)
                    .verifyCode(generatedString)
                    .build();
            memberVerifyCodeRepository.save(MemberVerifyCodeEntity.toEntity(memberVerifyCodeDTO));
        }

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

    public void updateVerificationCode(MemberVerifyCodeEntity entity, String generatedString) {
        entity.setVerifyCode(generatedString);
        memberVerifyCodeRepository.save(entity);
    }

    public void deleteById(String memberId) {
        memberVerifyCodeRepository.deleteByMemberId(memberId);
    }
}
