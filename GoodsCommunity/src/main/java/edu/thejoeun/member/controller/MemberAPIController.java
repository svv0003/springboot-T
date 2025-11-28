package edu.thejoeun.member.controller;

import edu.thejoeun.common.util.FileUploadService;
import edu.thejoeun.common.util.SessionUtil;
import edu.thejoeun.member.model.dto.Member;
import edu.thejoeun.member.model.service.MemberServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberAPIController {
    private final MemberServiceImpl memberService;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메세지 전송
    // private final FileUploadService fileUploadService;

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
      log.info("요청 데이터 - 이름 : {}, 이메일 : {}",member.getMemberName(), member.getMemberEmail());
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

    /**
     * fetchMyPageEditWithProfile(axios, formData, profileFile, navigate, setIsSubmitting);
     * @param updateData
     * @param session
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> updateMyPage(@RequestBody Map<String, Object> updateData, HttpSession session) {
        log.info("회원정보 수정 요청");
        try {
            Member m = new Member();
            m.setMemberPhone(updateData.get("memberPhone").toString());
            m.setMemberEmail(updateData.get("memberEmail").toString());
            m.setMemberName(updateData.get("memberName").toString());
            m.setMemberAddress(updateData.get("memberAddress").toString());
            // 새 비밀번호가 있는 경우
            String newPassword = (String) updateData.get("memberPassword");
            if (newPassword != null && !newPassword.isEmpty()) {
                m.setMemberPassword(newPassword);
            }
            // 현재 비밀번호
            String currentPassword = (String) updateData.get("currentPassword");
            Map<String, Object> res = memberService.updateMember(m, currentPassword, session);
            // 서비스에서 성공실패에 대한 결과를 res 담고 프론트엔드에 전달
            log.info("회원정보 수정 결과 : {}", res.get("message"));
            return res;
        } catch (Exception e) {
            log.error("서비스 접근했거나, 서비스 가기 전에 문제가 발생해서 회원정보 수정 실패 - 에러 : {} ", e.getMessage());
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", "회원정보 수정 중 오류가 발생했습니다.");
            return res;
        }
    }

    /*
    Controller 할 일
    요청을 받아 Service로 넘기고 결과를 응답하는 역할만 한다.
     */
    @PostMapping("/profile-image")
    public ResponseEntity<Map<String, Object>> uploadProfileImage(
            @RequestParam("file")MultipartFile file,
            @RequestParam("memberEmail") String memberEmail,
            HttpSession session){
        /*
        Map<String, Object> res = new HashMap<>();
        try{
            Member loginUser = SessionUtil.getLoginUser(session);
            if(loginUser == null) {
                res.put("success", false);
                res.put("message", "로그인이 필요합니다.");
                return res;
                // ResponseEntity 401
            }
            // 본인 확인
            if(!loginUser.getMemberEmail().equals(memberEmail)) {
                res.put("success", false);
                res.put("message", "본인 프로필만 수정 가능합니다.");
                return res;
                // ResponseEntity 403
            }
            // 파일 유효성 검증
            if(file.isEmpty()) {
                res.put("success", false);
                res.put("message", "파일이 비어있습니다.");
                return res;
                // ResponseEntity badRequest = 클라이언트가 하지 말라는 행동을 한 경우
            }
            // 이미지 파일 확인
            String contentType = file.getContentType();
            if(contentType == null || !contentType.contains("image/")) {
                res.put("success", false);
                res.put("message", "파일 이미지만 업로드 가능합니다.");
                return res;
                // ResponseEntity badRequest = 클라이언트가 하지 말라는 행동을 한 경우
            }
            // 이미지 파일 크기
            if(file.getSize() > 5 * 1024 * 1024) {
                res.put("success", false);
                res.put("message", "파일 크기는 5MB 초과할 수 없습니다.");
                return res;
                // ResponseEntity badRequest = 클라이언트가 하지 말라는 행동을 한 경우
            }
            // 기존 프로필 이미지 삭제
            if(loginUser.getMemberProfileImage() != null) {
                // 삭제 관련 기능 FileUploadService에서 작성 후 추가하기
                return res;
            }
            // 새 이미지 업로드
            String imageUrl = fileUploadService.uploadProfileImage(file);
            // DB 업데이트
            memberService.updateProfileImage(memberEmail, imageUrl);
            // 세션 업데이트
            loginUser.setMemberProfileImage(imageUrl);
            SessionUtil.setLoginUser(session, loginUser);

            res.put("success", true);
            res.put("message", "프로필 이미지 업데이트되었습니다.");
            res.put("imageUrl", imageUrl);
            log.info("프로필 이미지 업로드 성공 - 이메일 : {}, 파일명 : {}", memberEmail, file.getOriginalFilename());
            return res;
        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패 - 이메일 : {}, 오류 : {}", memberEmail, e.getMessage());
            res.put("success", false);
            res.put("message", "프로필 이미지 업로드 중 오류가 발생했습니다." + e.getMessage());
            return res;
            // 500 error - 백엔드 에러
        }
         */
        Map<String, Object> res = new HashMap<>();
        try{
            Member loginUser = SessionUtil.getLoginUser(session);
            String imageUrl = memberService.updateProfileImage(loginUser, memberEmail, file, session);
            res.put("success", true);
            res.put("message", "프로필 이미지 업데이트되었습니다.");
            res.put("imageUrl", imageUrl);
            log.info("프로필 이미지 업로드 성공 - 이메일 : {}, 파일명 : {}", memberEmail, file.getOriginalFilename());
            return ResponseEntity.ok(res);  // 업데이트가 무사히 완료되면 200 전달한다.
        } catch (IllegalStateException e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(401).body(res);
        } catch (SecurityException e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(403).body(res);
        } catch (IllegalArgumentException e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(res);
        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패 - 이메일 : {}, 오류 : {}", memberEmail, e.getMessage());
            res.put("success", false);
            res.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(res);
            // 500 error - 백엔드 에러
            /*
            개발자가 만든 exception은 회상위 exception이고,
            Java에서 기본으로 제공하는 exception은 최상위가 아닌 순부터 작성한다.
            exception의 부모인 Exception은 맨 마지막에 작성한다.
            부모 Exception까지 올 때 어떤 문제인지 파악 못하는 상태이다.
             */
        }
    }
}
