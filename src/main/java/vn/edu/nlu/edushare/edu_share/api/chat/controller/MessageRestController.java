package vn.edu.nlu.edushare.edu_share.api.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.request.MessageRequestDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.ConversationResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.service.ChatService;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class MessageRestController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ConversationResponseDto>> getUserConversations(@RequestParam String userId) {
        List<ConversationResponseDto> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<MessageResponseDto>> getUserMessages(@RequestParam Integer conversationId) {
        List<MessageResponseDto> messages = chatService.getUserMessages(conversationId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageResponseDto> sendMessage(@RequestBody MessageRequestDto chatMessageDto) {
        MessageResponseDto savedMessage = chatService.saveMessage(chatMessageDto);
        //khi bạn muốn trả dữ liệu về cho client, ví dụ danh sách tin nhắn.
        return ResponseEntity.ok(savedMessage);
    }

    @PutMapping("/messages/{conversationId}/read")
    public ResponseEntity<Integer> markMessageAsRead(@PathVariable Integer conversationId, @RequestParam String userId, @RequestParam String recipientId) {
        System.out.println("MARK READ API CALLED");
        System.out.println("conversationId = " + conversationId);
        System.out.println("userId = " + userId);

        int updatedRows = chatService.markMessagesAsRead(conversationId, userId, recipientId);

        System.out.println("updatedRows = " + updatedRows);

        return ResponseEntity.ok(updatedRows);
    }
}
