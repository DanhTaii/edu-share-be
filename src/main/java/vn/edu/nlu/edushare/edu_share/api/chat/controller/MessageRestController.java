package vn.edu.nlu.edushare.edu_share.api.chat.controller;

import org.springframework.data.domain.Page;
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

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<Page<MessageResponseDto>> getUserMessages(
            @PathVariable Integer conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<MessageResponseDto> messages = chatService.getUserMessages(conversationId, page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<MessageResponseDto> sendMessage(
            @PathVariable Integer conversationId,
            @RequestBody MessageRequestDto chatMessageDto
    ) {
        chatMessageDto.setConversationId(conversationId);
        MessageResponseDto savedMessage = chatService.saveMessage(chatMessageDto);
        return ResponseEntity.ok(savedMessage);
    }

    @PutMapping("/{conversationId}/messages/read")
    public ResponseEntity<Integer> markMessageAsRead(@PathVariable Integer conversationId, @RequestParam String userId, @RequestParam String recipientId) {
        int updatedRows = chatService.markMessagesAsRead(conversationId, userId, recipientId);
        return ResponseEntity.ok(updatedRows);
    }
}
