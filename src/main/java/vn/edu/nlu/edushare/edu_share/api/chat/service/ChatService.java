package vn.edu.nlu.edushare.edu_share.api.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.request.MessageRequestDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.ConversationResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Conversation;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Message;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.ConversationRepository;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.MessageRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    // SB tự động tạo ra sẵn công cụ và nạp sẵn cấu hình tần số /topic vào
    // Do @RequiredArgsConstructor nó đã tự nạp Dependency Injection vào rồi nên không cần @Autowired nữa
    // DI này là từ WebSocketConfig.java, nó sẽ tự động nạp vào SimpMessagingTemplate (SB tự động tạo ra)
    private final SimpMessagingTemplate messagingTemplate;

    public List<ConversationResponseDto> getUserConversations(String userId) {
        return conversationRepository.findUserConversations(userId);
    }

    public List<MessageResponseDto> getUserMessages(Integer conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public MessageResponseDto saveMessage(MessageRequestDto chatMessageDto) {
        try {
            // 1. Tìm Conversation
            Conversation conversation = conversationRepository.findById(chatMessageDto.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            // 2. Tạo và Lưu Message
            Message message = new Message();
            message.setConversation(conversation);
            message.setSenderId(chatMessageDto.getSenderId());
            message.setContent(chatMessageDto.getContent());
            message.setIsRead(false);

            Message savedMessage = messageRepository.save(message);

            // Cập nhật lastMessage và updatedAt của Conversation
            // 3. Cập nhật lại Conversation vỏ ngoài
            conversation.setLastMessage(savedMessage.getContent());
            conversation.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            conversationRepository.save(conversation);

            MessageResponseDto responseDto = new MessageResponseDto(
                    savedMessage.getId(),
                    conversation.getId(),
                    savedMessage.getSenderId(),
                    savedMessage.getContent(),
                    savedMessage.getIsRead(),
                    savedMessage.getCreatedAt()
            );

            //Tìm đúng đối tượng người nhận để gửi tin nhắn qua WebSocket (chỉ một người nhận sẽ nhận được tin nhắn này)
            String destination = "/topic/messages/" + chatMessageDto.getRecipientId();
            // 4. Gửi tin nhắn qua WebSocket đến người nhận
            messagingTemplate.convertAndSend(destination, responseDto);

            // 5. Trả về đúng object Response
            return responseDto;
        } catch (Exception e) {
            throw new RuntimeException("Error saving message: " + e.getMessage());
        }
    }

    @Transactional
    public int markMessagesAsRead(Integer conversationId, String userId) {
        return messageRepository.markMessagesAsRead(conversationId, userId);
    }
}
