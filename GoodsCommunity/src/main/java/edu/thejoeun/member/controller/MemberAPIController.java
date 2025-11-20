package edu.thejoeun.member.controller;

import edu.thejoeun.common.util.SessionUtil;
import edu.thejoeun.member.model.dto.Member;
import edu.thejoeun.member.model.service.MemberServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberAPIController {
    private  final MemberServiceImpl memberService;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메세지 전송


    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestBody Map<String, String> loginData, HttpSession session){
        String memberEmail = loginData.get("memberEmail");
        String memberPassword = loginData.get("memberPassword");
        Map<String, Object> res = memberService.loginProcess(memberEmail, memberPassword,session);
        return res;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session){
        return  memberService.logoutProcess(session);
    }

    @GetMapping("/check")
    public Map<String, Object> checkLoginStatus(HttpSession session){
        return memberService.checkLoginStatus(session);
    }

    // const res = axios.post("/api/auth/signup",signupData);
    // PostMapping 만들기
    // mapper.xml -> mapper.java -> service.java -> serviceImpl.java apiController.java
    // 완성

    @PostMapping("/signup")
    public void saveSignup(@RequestBody Member member){
      log.info("===회원가입 요청===");
      log.info("요청 데이터 - 이름 : {}, 이메일 : {}",member.getMemberName(),member.getMemberEmail());

      try {
          memberService.saveMember(member);
          log.info("회원가입 성공 - 이메일 : {}",member.getMemberEmail());
          //=======================================================================================
          // WebSocket 통해 실시간 알림 전송한다.
          Map<String, Object> notification = new HashMap<>();
          notification.put("msg", "새로 가입했습니다.");
          notification.put("name", member.getMemberName());
          notification.put("timestamp", System.currentTimeMillis());
          messagingTemplate.convertAndSend("/topic/notifications", notification);
          log.info("회원가입 및 WebSocket 알림 전송 완료 : {}", member.getMemberName()); // 개발자 회사 로그용
      } catch (Exception e){
          log.error("회원가입 실패 - 이메일 : {}, 에러 : {}",member.getMemberEmail(),e.getMessage());
        }
    }
}
