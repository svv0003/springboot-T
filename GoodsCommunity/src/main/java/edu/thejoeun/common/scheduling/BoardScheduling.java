package edu.thejoeun.common.scheduling;


import edu.thejoeun.common.scheduling.Service.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class BoardScheduling {
    private final SchedulingService schedulingService;
    @Scheduled(cron = "0 0 0 1 * *")
    public void updatePopularBoards(){
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("==== 인기글 업데이트 스케줄러 시작 [{}] ====", startTime);
        try {
          int result =  schedulingService.updatePopularBoards();
           log.info("인기글 업데이트 완료 : {} 건", result);
        }catch (Exception e){
         log.error("인기글 업데이트 중 오류 발생 : ", e);
        }
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("==== 인기글 업데이트 스케줄러 종료 [{}] ====", endTime);
    }
}





