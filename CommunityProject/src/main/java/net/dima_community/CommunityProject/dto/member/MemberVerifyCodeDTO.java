package net.dima_community.CommunityProject.dto.member;

import java.lang.reflect.Member;

import lombok.Builder;
import lombok.Getter;
import net.dima_community.CommunityProject.entity.member.MemberVerifyCodeEntity;

@Builder
@Getter
public class MemberVerifyCodeDTO {
    public Long memberVerifyCodeSeq;
    public String memberId;
    public String verifyCode;

    public static MemberVerifyCodeDTO toDTO(MemberVerifyCodeEntity memberVerifyCodeEntity) {
        return MemberVerifyCodeDTO.builder()
                .memberVerifyCodeSeq(memberVerifyCodeEntity.getMemberVerifyCodeSeq())
                .memberId(memberVerifyCodeEntity.getMemberId())
                .verifyCode(memberVerifyCodeEntity.getVerifyCode())
                .build();
    }
}
