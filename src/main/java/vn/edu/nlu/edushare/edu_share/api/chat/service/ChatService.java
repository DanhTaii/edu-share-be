package vn.edu.nlu.edushare.edu_share.api.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.request.ConversationCreateRequestDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.request.MessageRequestDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.ConversationResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageStatusResponse;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Conversation;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Message;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.ConversationRepository;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.MessageRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.nlu.edushare.edu_share.api.notification.service.NotificationService;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;
import vn.edu.nlu.edushare.edu_share.api.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    // SB tự động tạo ra sẵn công cụ và nạp sẵn cấu hình tần số /topic vào
    // Do @RequiredArgsConstructor nó đã tự nạp Dependency Injection vào rồi nên không cần @Autowired nữa
    // DI này là từ WebSocketConfig.java, nó sẽ tự động nạp vào SimpMessagingTemplate (SB tự động tạo ra)
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

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

            // Kiểm tra xem có phải người trong đoạn hội thoại k và lấy TÊN NGƯỜI GỬI
            String senderId = chatMessageDto.getSenderId();
            String actualRecipientId;
            String senderName; // Thêm biến để chứa tên người gửi

            if (senderId.equals(conversation.getUserOne().getId())) {
                actualRecipientId = conversation.getUserTwo().getId();
                senderName = conversation.getUserOne().getFullName(); // Lấy tên User 1
            } else if (senderId.equals(conversation.getUserTwo().getId())) {
                actualRecipientId = conversation.getUserOne().getId();
                senderName = conversation.getUserTwo().getFullName(); // Lấy tên User 2
            } else {
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

            // 5. GỬI THÔNG BÁO PUSH QUA FCM CHO NGƯỜI NHẬN KHI ĐÃ TẮT APP (WEBSOCKET ĐÃ END)
            notificationService.sendChatNotification(
                    actualRecipientId,
                    "Tin nhắn từ " + senderName,
                    responseDto.getContent(),
                    String.valueOf(conversation.getId())
            );

            // 6. Trả về đúng object Response
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

    @Transactional
    public MessageResponseDto createConversationAndSendFirstMessage(ConversationCreateRequestDto request) {
        // 1. Sắp xếp ID để chống trùng lặp phòng chat (đã bàn ở lượt trước)
        String user1 = request.getSenderId().compareTo(request.getRecipientId()) < 0 ? request.getSenderId() : request.getRecipientId();
        String user2 = request.getSenderId().compareTo(request.getRecipientId()) > 0 ? request.getSenderId() : request.getRecipientId();

        // 2. Tìm phòng chat cũ hoặc tạo mới nếu chưa có
        Conversation conversation = conversationRepository.findByUserOneIdAndUserTwoIdAndPostId(user1, user2, request.getPostId())
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    // SỬA Ở ĐÂY: Dùng getReferenceById để lấy Proxy Object (Không tốn câu SELECT)
                    User u1 = userRepository.getReferenceById(user1);
                    User u2 = userRepository.getReferenceById(user2);

                    newConv.setUserOne(u1);
                    newConv.setUserTwo(u2);

                    newConv.setPostId(request.getPostId());
                    // Nếu bảng của bạn lưu tham chiếu User (Entity), nhớ fetch User từ DB ra set vào nhé
                    return conversationRepository.save(newConv);
                });

        // 3. Tạo và lưu tin nhắn đầu tiên
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(request.getSenderId());
        message.setContent(request.getContent());
        message.setIsRead(false);
        Message savedMessage = messageRepository.save(message);

        // 4. Cập nhật last_message cho phòng chat
        conversation.setLastMessage(request.getContent());
        conversation.setUpdatedAt(savedMessage.getCreatedAt());
        conversationRepository.save(conversation);

        // 5. Chuyển sang DTO để trả về
        MessageResponseDto responseDto = new MessageResponseDto(
                savedMessage.getId(),
                conversation.getId(),
                savedMessage.getSenderId(),
                savedMessage.getContent(),
                savedMessage.getIsRead(),
                savedMessage.getCreatedAt()
        );

        // 6. Broadcast Real-time qua WebSocket cho người nhận (Người bán)
        messagingTemplate.convertAndSend("/topic/messages/" + request.getRecipientId(), responseDto);

        // 7. GỬI FCM CHO TIN NHẮN ĐẦU TIÊN
        // Lấy tên người gửi ra để làm tiêu đề thông báo
        // Việc gọi getFullName() trên Proxy sẽ tự động trigger truy vấn để lấy tên
        String senderName = request.getSenderId().equals(conversation.getUserOne().getId())
                ? conversation.getUserOne().getFullName()
                : conversation.getUserTwo().getFullName();

        notificationService.sendChatNotification(
                request.getRecipientId(), // Gửi tới người bán
                "Tin nhắn từ " + senderName,
                request.getContent(),
                String.valueOf(conversation.getId())
        );

        return responseDto;
    }

    public Integer getConversationIdBetweenUsers(String senderId, String recipientId, int postId) {
        return conversationRepository.findConversationIdBetweenUsers(senderId, recipientId, postId);
    }

}
