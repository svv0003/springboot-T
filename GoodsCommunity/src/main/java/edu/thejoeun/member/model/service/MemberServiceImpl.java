package edu.thejoeun.member.model.service;


import edu.thejoeun.common.util.SessionUtil;
import edu.thejoeun.member.model.dto.Member;
import edu.thejoeun.member.model.mapper.MemberMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MemberServiceImpl  implements MemberService {
    @Autowired
    private MemberMapper memberMapper;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public Member login(String memberEmail, String memberPassword) {
        Member member = memberMapper.getMemberByEmail(memberEmail);
        if(member == null) {
            return null;
        }
        if(!bCryptPasswordEncoder.matches(memberPassword, member.getMemberPassword())) {
            return null;
        }
        member.setMemberPassword(null);
        return member;
    }

    @Override
    public void saveMember(Member member) {
        String originPW = member.getMemberPassword(); // 기존 클라이언트 비밀번호 가져오기
        String encodedPw = bCryptPasswordEncoder.encode(originPW); // 비밀번호 암호화
        member.setMemberPassword(encodedPw); // 암호화처리된 비밀번호로 교체

        // 교체된 비밀번호 포함해서 저장
        // 비밀번호 암호화해서 저장
        memberMapper.saveMember(member);
    }

    public Map<String, Object> loginProcess(String memberEmail, String memberPassword, HttpSession session) {
        Map<String, Object> res = new HashMap<>();
        Member m = login(memberEmail,memberPassword);
        if(m == null) {
            res.put("success",false);
            res.put("message","이메일 또는 비밀번호가 일치하지 않습니다.");
            log.warn("로그인 실패: {}", memberEmail);
            return  res;
        }
        SessionUtil.setLoginUser(session, m);
        res.put("success",true);
        res.put("message","로그인 성공");
        res.put("user",m);
        log.info("로그인 성공 : {}",m.getMemberEmail());
        return res;
    }

    /**
     * 로그아웃 처리
     * @param  session 로그인된 세션 정보 가져와서 로그아웃 처리 후
     * @return 처리결과 반환
     */
    public  Map<String, Object> logoutProcess(HttpSession session) {
        Map<String, Object> res = new HashMap<>();
        SessionUtil.invalidateLoginUser(session);
        res.put("success",true);
        res.put("message","로그아웃 성공");

        return res;
    }

    /**
     * 로그인 상태확인
     * @param session 현재 세션을 가져온 후
     * @return 로그인 이 되어있으면 로그인이 되어있는 상태로 반환
     */
    public Map<String, Object> checkLoginStatus(HttpSession session) {
        Map<String, Object> res = new HashMap<>();
        Member loginUser = (Member) session.getAttribute("loginUser");

        if(loginUser == null) {
            res.put("loggedIn", false);
            res.put("user", null);
            log.debug("로그인 상태 확인: 로그인되지 않음");
        }else {
            res.put("loggedIn", true);
            res.put("user",loginUser);
            log.debug("로그인 상태 확인 : {}", loginUser.getMemberEmail());
        }
        return  res;
    }
}