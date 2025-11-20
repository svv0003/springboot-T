package edu.thejoeun.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 공개 채팅방 메세지 처리
     * 클라이언트가 /app/chat.sendMessage로 메세지를 보내면
     * /topic/public을 구독한 모든 사용자에게 브로드캐스트한다.
     * @param chatMsg 채팅 메세지 (sender, content, type 포함)
     * @return 처리된 채팅 메세지
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Map<String, Object> sendMessage(Map<String, Object> chatMsg) {
        // 현재 시간 추가한다.
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        chatMsg.put("timestamp", timeStamp);
        log.info("채팅 메세지 수신 - 발신자 : {}, 내용,{}",
                chatMsg.get("sender"), chatMsg.get("content"));
        return chatMsg;
    }

    /**
     * 새로운 사용자가 채팅방에 입장할 때 호출한다.
     * @param chatMsg 입장 메세지 (sender, type=JSON)
     * @return 입장 알림 메세지
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Map<String, Object> addUser(Map<String, Object> chatMsg) {
        // 현재 시간 추가한다.
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        chatMsg.put("timestamp", timeStamp);
        log.info("채팅방 입장 - 사용자 : {}", chatMsg.get("sender"));
        return chatMsg;
    }

    /**
     * 특정 사용자에게 개인 메세지 전송
     * @param username 수신자 사용자명
     * @param message 메세지 내용
     * @return
     */
    @MessageMapping("/chat.addMessage")
    @SendTo("/topic/public")
    public void sendPrivateMessage(String username, Map<String, Object> message) {
        // 현재 시간 추가한다.
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        message.put("timestamp", timeStamp);
        messagingTemplate.convertAndSendToUser(username,"/queue/private", message);
        log.info("개인 메세지 전송 - 수신자 : {}, 내용 : {}", username, message.get("content"));
    }
}
