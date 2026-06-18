package vn.edu.nlu.edushare.edu_share.api.chat.dto.request;

import lombok.Data;

@Data
public class ConversationCreateRequestDto {
    private String senderId;
    private String recipientId;
    private int postId;
    private String content;
}
