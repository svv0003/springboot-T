package edu.thejoeun.member.controller;


import edu.thejoeun.member.model.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SessionAttributes("{authKey}")
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/signup") // api : /email/signup
    public int signup(@RequestBody String email){
        boolean isSuccess = emailService.sendMail("signup", email);
        if(isSuccess){
            return 1;
        }
        return 0;
    }

    @PostMapping("/checkAuthKey")
    public int checkAuthKey(@RequestBody Map<String, Object> map) {
        return emailService.checkAuthKey(map);
    }
}