package edu.thejoeun.common.aop;

import edu.thejoeun.member.model.dto.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
/*
 * @Component @Bean 등록까지 함께 들어있는 어노테이션
 *            개발자가 만든 파일을 스프링부트에서 자체적으로 관리하도록 세팅
 *            Service Mapper Controller 처럼 특정 기능으로 분류지을 수 없는 객체 파일 문서
 * @Aspect 공통 관심사가 작성된 클래스임 명시(AOP 동작용 클래스)
 * @Slf4j  log를 찍을 수 있는 객체를 생성코드 추가 (lombok 회사에서 제공하는 어노테이션)
 * */

/*
advice : 끼워넣을 코드 (= 기능)
pointCut :  실제 advice 를 적용할 joinPoint(지점)

PointCut 작성 방법
 execution ( [접근제한자(생략가능)] 리턴타입 클래스명 메서드명 ([파라미터])

 클래스명은 패키지명부터 모두 작성

 * : 모든
 // 메서드명(..) 에서 매개변수 .. : 매개변수 0~n 개 (개수 상관 없음)
 */

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Before("PointcutBundle.controllerPointCut()")
    public void beforeController(JoinPoint jp) {
        String className = jp.getTarget().getClass().getSimpleName();
        String methodName = jp.getSignature().getName() + "()";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s.%s] 요청 ", className, methodName));
        try{
            // HTTP 요청 컨텍스트가 있는 경우에만 접근
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes!=null){
                HttpServletRequest request = attributes.getRequest();
                String ip = getRemoteAddr(request);
                sb.append(String.format(" / ip : %s", ip));
                if(request.getSession().getAttribute("loginMember") != null) {
                    String memberEmail =
                            ( (Member)request.getSession().getAttribute("loginMember") ).getMemberEmail();
                    sb.append(String.format(", 요청 회원 : %s", memberEmail));
                } else {
                    sb.append(" / Websocket or Async Request");
                }
            }
        } catch (IllegalStateException e) {
            sb.append(" /NO HTTP Request Context (WebSocket)");
        }
        log.info(sb.toString());
    }








    @Around("PointcutBundle.serviceImplPointCut()")
    public Object aroundServiceImpl(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName() + "()";
        log.info("========== {}.{} 서비스 호출 ==========", className, methodName);
        log.info("Parameter : {}", Arrays.toString(pjp.getArgs()));
        long startMs = System.currentTimeMillis();
        Object obj = pjp.proceed();
        long endMs = System.currentTimeMillis();
        log.info("Running Time : {}ms", endMs - startMs);
        log.info("===================================================================================");
        return obj;
    }



    @AfterThrowing(pointcut = "@annotation(org.springframework.transaction.annotation.Transactional)",
            throwing = "ex")
    public void transactionRollback(JoinPoint jp, Throwable ex) {
        log.info("***** 트랜잭션이 롤백됨 {} *****", jp.getSignature().getName());
        log.error("[롤백 원인] : {}", ex.getMessage());
    }


    private String getRemoteAddr(HttpServletRequest request) {
        String ip = null;
        ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}


