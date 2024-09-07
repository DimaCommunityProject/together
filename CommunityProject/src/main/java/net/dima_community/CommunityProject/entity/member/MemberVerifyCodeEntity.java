package net.dima_community.CommunityProject.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dima_community.CommunityProject.dto.member.MemberVerifyCodeDTO;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "member_verify_code")
public class MemberVerifyCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_verify_code_seq")
    private Long memberVerifyCodeSeq;

    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "member_id")
    @Column(name = "member_id")
    public String memberId;

    @Column(name = "verify_code")
    public String verifyCode;

    public static MemberVerifyCodeEntity toEntity(MemberVerifyCodeDTO memberVerifyCodeDTO) {
        return MemberVerifyCodeEntity.builder()
                .memberVerifyCodeSeq(memberVerifyCodeDTO.getMemberVerifyCodeSeq())
                .memberId(memberVerifyCodeDTO.getMemberId())
                .verifyCode(memberVerifyCodeDTO.getVerifyCode())
                .build();
    }

}
