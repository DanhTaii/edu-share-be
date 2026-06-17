package vn.edu.nlu.edushare.edu_share.api.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageStatusResponse {
    private Integer conversationId;
    private String senderId;
    private Boolean isRead;
}
