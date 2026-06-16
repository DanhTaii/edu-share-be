package vn.edu.nlu.edushare.edu_share.api.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//Tự sinh ra Getter, Setter
@Data
//Constructor không tham số
@NoArgsConstructor
//Constructor có tham số
@AllArgsConstructor
public class MessageRequestDto implements Serializable {
    private Integer conversationId;
    private String senderId;
    //người nhận
    private String recipientId;
    private String content;

}
