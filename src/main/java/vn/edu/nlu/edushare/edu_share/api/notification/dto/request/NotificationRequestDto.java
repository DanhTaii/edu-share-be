package vn.edu.nlu.edushare.edu_share.api.notification.dto.request;

import lombok.Data;

@Data
public class NotificationRequestDto {
    String recipientId;
    String senderName;
    String itemName;
    Integer postId;
}
