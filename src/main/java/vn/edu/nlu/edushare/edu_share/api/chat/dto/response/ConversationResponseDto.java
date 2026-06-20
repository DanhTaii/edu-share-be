package vn.edu.nlu.edushare.edu_share.api.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class ConversationResponseDto {

    private Integer conversationId;
    private Integer postId;
    private String recipientId;
    private String recipientName;
    private String recipientAvatarUrl;
    private String lastMessage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Timestamp updatedAt;
    private int unreadCount;

    // Do unreadCount có thể là Long nên cần constructor riêng để chuyển đổi
    public ConversationResponseDto(Integer conversationId, Integer postId, String recipientId, String recipientName, String recipientAvatarUrl, String lastMessage, Timestamp updatedAt, Long unreadCount) {
        this.conversationId = conversationId;
        this.postId = postId;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.recipientAvatarUrl = recipientAvatarUrl;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
        this.unreadCount = Math.toIntExact(unreadCount);
    }

    public ConversationResponseDto(Integer conversationId, Integer postId, String recipientId, String recipientName, String recipientAvatarUrl, String lastMessage, Timestamp updatedAt, int unreadCount) {
        this.conversationId = conversationId;
        this.postId = postId;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.recipientAvatarUrl = recipientAvatarUrl;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
        this.unreadCount = Math.toIntExact((long) unreadCount);
    }
}
