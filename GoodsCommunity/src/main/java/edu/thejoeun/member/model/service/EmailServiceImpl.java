package edu.thejoeun.member.model.service;

import edu.thejoeun.member.model.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional // 예외 발생하면 롤백할게 (기본값으로 커밋)
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired // EmailConfig 에  설정된 메일보내기 기능 과 관련 환경설정 사용
    private JavaMailSender mailSender;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired // 템플릿 엔진 이용해서 auth/signup.html 에 있는 html 코드를 java로 변환
    private SpringTemplateEngine templateEngine;

    // 이메일 보내기
    @Override
    public String sendMail(String htmlName, String email) {

        // 6자리 난수 코드 생성하는 기능 불러오기
        String authKey = createAuthKey();

        try{
            // 제목
            String 제목 = null;

            switch (htmlName){
                case "signup" :
                    제목 = "[나의 프로젝트 명칭 / 회사 명칭] 회원가입 인증번호 입니다.";
                    break;
            }

            // 인증 메일 보내기
            // MimeMessage : Java에서 메일을 보내기위해 메세지를 준비하는 객체
            // import jakarta.mail.internet.MimeMessage;
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            //                                            메세지내용, 파일전송사용?, 문자인코딩지정
            // mailSender 객체를 이용해서 이메일 본문을쉽게 설정할 수 있도록 도와줌
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
            helper.setTo(email);     // 이메일 받을 사람 지정
            helper.setSubject(제목); // 이메일 제목 지정
            helper.setText(loadHtml(authKey,htmlName),true);
            // 이메일 본문 인증키 + 회사에서 인증키를 보냈다는 신뢰의 본문 내용  true 는 html 형식이 맞다
            // 메일에 이미지 첨부 파일 첨부랑은 살짝 다름
            //      로고이미지를 보낼 때 보낼이미지와 이미지를 보낼 때 담을 id 명칭 작성
            //                이미지를 담고 갈 바구니 명칭, 이미지 본문 내용
            helper.addInline("logo"             , new ClassPathResource("static/images/logo.jpg"));
            // 모든 준비가 끝나면 진짜로 메일보내기~!
            mailSender.send(mimeMessage); // 이메일 발송
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        // map 이름에 key -value 형태로 "인증키" : 6자리 , "email" : 인증번호 받은 사람의 이메일
        // 형태로 잠시 자바에서 보관
        Map<String, String> map = new HashMap<>();
        map.put("authKey",authKey);
        map.put("email",email);

        // 이전에 해당 이메일로 인증키를 발송한 기록이 있다면 키를 갱신
        int result = emailMapper.updateAuthKey(map); // 발송한 기록이 없으면 0이 result 담겨질 것

        if(result == 0){ // 이전에 인증키를 보낸적이 없음
            result = emailMapper.insertAuthKey(map); // 새로운 인증키 기록을 삽입하는 매퍼 메소드 호출
            // 이메일에서 인증키를 무사히 보냈다면 result 에는 1이 담길 것
        }
        // 둘다 실패하여 최종 result 가 0 이라면 null 반환
        if(result == 0){
            return null;
        }
        // 이메일 발송 & 인증키 잠시 보관을 모두 성공하면 생성된 인증키 반환
        return authKey;
    }

        /*
        필요한 타입:Map<String,Object>
        제공된 타입:Map<String,String>

        Map<String, String> map = new HashMap<>();

        int updateAuthKey(Map<String, Object> map) ;

        emailMapper.updateAuthKey()  내부는 String, Object 형태이고,
        매개변수에 들어갈 데이터는          String, String 형태로 매개변수 내부 자료형 데이터와
        매개변수 내부에 들어갈 데이터 자료형이 일치하지 않아 발생한 빨간줄
         */














    // HTML 파일을 읽어와 String으로 변환(타임리프 템플릿 사용해서 html 가져오기)
    // import org.thymeleaf.context.Context;
    public String loadHtml(String authKey, String htmlName) {
        Context context = new Context() ;

        // 타임리프가 적용된 html에서 사용할 값 추가
        context.setVariable("authKey", authKey);

        // templates/pages/auth 폴더에서 htmlName 과 같은
        // .html 파일 내용을 읽어와 String으로 변환
        //  return templateEngine.process("pages/auth" + htmlName, context);
        //  pages/authsignup.html
        //  return templateEngine.process("pages/auth/" + htmlName, context);
        //  pages/auth/signup.html
        return templateEngine.process("pages/auth/" + htmlName, context);
    }

    /**
     * 인증번호 생성(영어 대문자 + 소문자  + 숫자 6자리
     * @return authKey
     */
    public String createAuthKey(){
        String key = ""; // 6자리 숫자 문자 혼합을 담을 빈 문자열 공간 생성

        for(int i=0; i<6; i++){ // key 의 내부가 6자리로 채워지기 위해 0 ~ 5까지 5번 반복

            /*********
             * 만약에 우리 팀이 숫자만 랜덤으로 출력하겠다!
             * for 문 안에 if else 지우기
             *  int num = (int)(Math.random()*10);
             *  key +=num; 만 살려두기
             *
             *  우리팀이 영어만 랜덤으로 출력하겠다.
             *   char ch=(char)(Math.random()*26 + 65);
             *   ch = (char)(ch + ('a' - 'A'));
             *   key += ch;
             *
             *   영어중에서 대문자만 랜덤으로 6자리 출력하겠다.
             *   char ch=(char)(Math.random()*26 + 65);
             *   key += ch;
             *   만 살려두기
             *
             *   영어중에서 소문자만 랜덤으로 6자리 출력하겠다.
             *   char ch=(char)(Math.random()*26 + 65);
             *   ch = (char)(ch + ('a' - 'A'));
             *   key += ch;
             *   만 살려두기
             *********/


            // 0,1,2 중 하나의 무작위정수 뽑기 뽑힌 숫자는 sel1 안에 저장
            // 만약에 0 이 뽑히면 숫자    1,2 영어(대문자 or 소문자)
            int sel1 = (int)(Math.random()*3); // 0: 숫자 / 1,2 영어


            if(sel1==0) { // sel1 뽑인 숫자가 0이면 숫자 당첨~!

                int num = (int)(Math.random()*10); // 0~9
                key +=num; // key 내부에 랜덤으로 뽑힌 숫자 하나가 들어감
            } else { // 0 이 아니면 영어 ( 대 소문자이기 때문에 else if 가 아니라 else 로 처리
                // 우선은 무작위로 A부터 Z까지 대문자를 생성해서 ch 저장
                char ch=(char)(Math.random()*26 + 65); // A~Z (65 ~ 65+26)

                // 또 랜덤뽑기를 해서 0이 나오면 소문자~ 1이 나오면 대문자~
                int sel2 = (int)(Math.random() * 2); // 0 : 소문자 / 1: 대문자

                if(sel2==0) {
                    ch = (char)(ch + ('a' - 'A')); // 대문자를 소문자로 변경
                }

                key += ch; // 위에서 랜덤의 랜덤으로 생성된 소문자나 대문자를 key 추가
            }
        }
        return key;
    }

    // 이메일, 인증번호 확인
    @Override
    public int checkAuthKey(Map<String, Object> map) {
        log.info("service map data: {}",map);
        return emailMapper.checkAuthKey(map);
    }
}