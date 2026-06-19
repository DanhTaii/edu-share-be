package vn.edu.nlu.edushare.edu_share.api.user_fcm_token.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.dto.SaveFcmTokenRequest;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.service.UserFcmTokenService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fcm/tokens")
@RequiredArgsConstructor
public class UserFcmTokenController {

    @Autowired
    private final UserFcmTokenService notificationService;

    /* API để thiết bị di động (Android/iOS) gửi FCM Token lên Server.
     * @param userId ID của người dùng đang đăng nhập
     * @param token  Chuỗi FCM Token do Google Firebase cấp cho thiết bị
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> updateFcmToken(
            @RequestBody SaveFcmTokenRequest fcmTokenRequest
    ) {
        // Gọi Service để xử lý logic: Nếu token mới thì thêm, nếu máy đổi chủ thì cập nhật
        notificationService.saveOrUpdateFcmToken(fcmTokenRequest.getUserId(), fcmTokenRequest.getFcmToken());

        // Trả về JSON phản hồi thành công
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Đã cập nhật FCM Token cho người dùng: " + fcmTokenRequest.getUserId());

        return ResponseEntity.ok(response);
    }

}
