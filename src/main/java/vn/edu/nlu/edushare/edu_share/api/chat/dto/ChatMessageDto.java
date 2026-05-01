package vn.edu.nlu.edushare.edu_share.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    // 1. Gắn với món đồ nào? (VD: ID của cuốn giáo trình)
    private Integer postId;

    // 2. Ai gửi? (Firebase UID của người đang bấm nút gửi)
    private String senderId;

    // 3. Gửi cho ai? (Firebase UID của chủ món đồ)
    private String recipientId;

    // 4. Gửi cái gì? (Nội dung tin nhắn: "Sách này còn không bạn?")
    private String content;

    // 5. Loại tin nhắn (Không bắt buộc, nhưng nên có để mở rộng sau này)
    private String type; // Thường là "CHAT", "JOIN", "LEAVE"
}