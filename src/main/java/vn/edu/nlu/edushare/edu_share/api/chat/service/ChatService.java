package vn.edu.nlu.edushare.edu_share.api.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.request.MessageRequestDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.ConversationResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageStatusResponse;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Conversation;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Message;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.ConversationRepository;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.MessageRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<ConversationResponseDto> getUserConversations(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return conversationRepository.findUserConversations(userId, pageable);
    }

    public Page<MessageResponseDto> getUserMessages(Integer conversationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId, pageable);
    }

    @Transactional
    public MessageResponseDto saveMessage(MessageRequestDto chatMessageDto) {
        try {
            // 1. Tìm Conversation
            Conversation conversation = conversationRepository.findById(chatMessageDto.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            // Kiểm tra xem có phải người trong đoạn hội thoại k
            String senderId = chatMessageDto.getSenderId();
            String actualRecipientId;

            // Nếu người gửi là User 1, thì người nhận chắc chắn là User 2
            if (senderId.equals(conversation.getUserOne().getId())) {
                actualRecipientId = conversation.getUserTwo().getId();
            }
            // Ngược lại, nếu người gửi là User 2, thì người nhận là User 1
            else if (senderId.equals(conversation.getUserTwo().getId())) {
                actualRecipientId = conversation.getUserOne().getId();
            }
            // Nếu không phải 1 trong 2 người này -> Kẻ đột nhập!
            else {
                throw new SecurityException("Bạn không thuộc phòng chat này!");
            }

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
            String destination = "/topic/messages/" + actualRecipientId;
            // 4. Gửi tin nhắn qua WebSocket đến người nhận
            messagingTemplate.convertAndSend(destination, responseDto);

            // Bắn ngược lại cho cả thiết bị khác của người gửi (nếu có)
            String destinationSender = "/topic/messages/" + senderId;
            messagingTemplate.convertAndSend(destinationSender, responseDto);

            // 5. Trả về đúng object Response
            return responseDto;
        } catch (Exception e) {
            throw new RuntimeException("Error saving message: " + e.getMessage());
        }
    }

//    @Transactional
    public int markMessagesAsRead(Integer conversationId, String userId, String recipientId) {
        String destinationStatus = "/topic/receipts/" + recipientId;

        int result = messageRepository.markMessagesAsRead(conversationId, userId);
        if (result > 0) {
            MessageStatusResponse statusResponse = new MessageStatusResponse(conversationId, userId, true);
            messagingTemplate.convertAndSend(destinationStatus, statusResponse);
        }

        return result;
    }
}
