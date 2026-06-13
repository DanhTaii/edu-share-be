package vn.edu.nlu.edushare.edu_share.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private Integer id;
    private Integer conversationId;
    private String senderId;
    private String content;
    private Boolean isRead;
    private Timestamp createdAt;
}