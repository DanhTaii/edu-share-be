package vn.edu.nlu.edushare.edu_share.api.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class ConversationResponseDto {

    private Integer conversationId;
    private String recipientId;
    private String recipientName;
    private String recipientAvatarUrl;
    private String lastMessage;
    private Timestamp updatedAt;
    private int unreadCount;

    // Do unreadCount có thể là Long nên cần constructor riêng để chuyển đổi
    public ConversationResponseDto(Integer conversationId, String recipientId, String recipientName, String recipientAvatarUrl, String lastMessage, Timestamp updatedAt, Long unreadCount) {
        this.conversationId = conversationId;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.recipientAvatarUrl = recipientAvatarUrl;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
        this.unreadCount = Math.toIntExact(unreadCount);
    }

    public ConversationResponseDto(Integer conversationId, String recipientId, String recipientName, String recipientAvatarUrl, String lastMessage, Timestamp updatedAt, int unreadCount) {
        this.conversationId = conversationId;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.recipientAvatarUrl = recipientAvatarUrl;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
        this.unreadCount = Math.toIntExact((long) unreadCount);
    }
}
