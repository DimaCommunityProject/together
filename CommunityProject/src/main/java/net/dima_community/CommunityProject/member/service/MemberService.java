package net.dima_community.CommunityProject.member.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.common.port.BCryptEncoderHolder;
import net.dima_community.CommunityProject.common.port.DBConnector;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.member.service.port.MemberRepository;

@Service
@Slf4j
@Builder
@RequiredArgsConstructor
public class MemberService {

    // public final Member member;
    public final MemberRepository memberRepository;
    public final BCryptEncoderHolder bCryptEncoderHolder;
    public final DBConnector dbConnector;

    // SQL Injection 대비 PreparedStatement 사용
    public boolean findByIdThroughConn(String id) {
        Connection conn = dbConnector.getConnection();
        if (conn != null)
            log.info("conn 생성");

        String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return false; // 이미 있는 아이디
            }
            return true; // 사용 가능한 아이디

        } catch (SQLException e) {
            log.info("SQLException 발생!");
            e.printStackTrace();
            return false;
        }
    }

    public Member findById(String id) {
        Optional<Member> result = memberRepository.findById(id);
        if (result == null | !result.isPresent()) {
            throw new ResourceNotFoundException("Member", id);
        }
        return result.get();
        // return memberRepository.findById(id).orElseThrow(() -> new
        // ResourceNotFoundException("Member", id));
    }

    public void saveMemberWithVerificationCode(Member member, String verifyCode) {
        Member result = member.updateVerifyCode(verifyCode);
        memberRepository.save(result);
    }

    public boolean verifyMemberByCode(String to, String code) {
        Optional<Member> result = memberRepository.findByEmail(to);
        if (result == null || !result.isPresent()) {
            throw new ResourceNotFoundException("Member", to);
        }
        Member member = result.get();
        if (!code.equals(member.getMemberVerifyCode())) {
            // memberRepository.delete(member);
            return false;
        }
        return true;
    }

    public Member setEncodedPassword(Member member) {
        member.setEncodedPassword(bCryptEncoderHolder);
        return member;

    }

    public void approve(String id) {
        Member member = findById(id);
        member.enabledToYes();
        memberRepository.save(member);
    }

    public Member updateMember(String memberId, String memberName, String memberEmail) {
        Member member = null;
        // try {
        log.info("updateMember 도착");
        member = findById(memberId);
        // } catch (ResourceNotFoundException e) {
        // return member;
        // }
        Member updatedMember = member.update(memberName, memberEmail);
        memberRepository.save(updatedMember);
        return updatedMember;
    }

}
