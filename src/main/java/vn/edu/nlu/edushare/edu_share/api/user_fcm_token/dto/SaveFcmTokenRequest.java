package vn.edu.nlu.edushare.edu_share.api.user_fcm_token.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//Constructor không tham số
@NoArgsConstructor
//Constructor có tham số
@AllArgsConstructor
public class SaveFcmTokenRequest {
    private String userId;
    private String fcmToken;
}
