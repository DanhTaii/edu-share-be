package vn.edu.nlu.edushare.edu_share.api.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vn.edu.nlu.edushare.edu_share.api.chat.service.ChatService;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload ChatMessageDto chatMessageDto) {
//        Message saveMsg = chatService.processAndSaveMessage(chatMessageDto);
//
//        messagingTemplate.convertAndSendToUser(
//                chatMessageDto.getRecipientId(),
//                "/queue/messages",
//                chatMessageDto
//        );
//    }
}
