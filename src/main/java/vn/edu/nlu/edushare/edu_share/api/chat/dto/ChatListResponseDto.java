package vn.edu.nlu.edushare.edu_share.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatListResponseDto {

    private Integer conversationId;
    private String partnerId;
    private String partnerName;
    private String partnerAvatarUrl;
    private String lastMessage;
    private LocalDateTime updatedAt;

}
