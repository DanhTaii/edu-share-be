package vn.edu.nlu.edushare.edu_share.api.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.ChatListResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.ChatMessageDto;
import vn.edu.nlu.edushare.edu_share.api.chat.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class MessageRestController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatListResponseDto>> getUserConversations(@RequestParam String userId) {
        List<ChatListResponseDto> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageDto>> getUserMessages(@RequestParam Integer conversationId) {
        List<ChatMessageDto> messages = chatService.getUserMessages(conversationId);
        return ResponseEntity.ok(messages);
    }
}
