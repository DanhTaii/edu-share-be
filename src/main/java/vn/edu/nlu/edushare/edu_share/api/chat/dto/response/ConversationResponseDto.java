package vn.edu.nlu.edushare.edu_share.api.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponseDto {

    private Integer conversationId;
    private String recipientId;
    private String recipientName;
    private String recipientAvatarUrl;
    private String lastMessage;
    private Timestamp updatedAt;

}
