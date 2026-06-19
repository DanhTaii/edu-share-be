package vn.edu.nlu.edushare.edu_share.api.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.model.UserFcmToken;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.repository.UserFcmTokenRepository;
import vn.edu.nlu.edushare.edu_share.infrastructure.firebase.service.FcmService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserFcmTokenRepository tokenRepository;
    private final FcmService fcmService; // Gọi thằng dịch vụ thuần Google

    /**
     * Xử lý cho tính năng CHAT
     */
    public void sendChatNotification(String recipientId, String senderName, String content, String conversationId) {
        // 1. Lấy danh sách token từ DB chuyển thành list chuỗi String thuần
        List<String> tokens = tokenRepository.findByUserId(recipientId)
                .stream().map(UserFcmToken::getFcmToken).toList();

        // 2. Chuẩn bị data đặc trưng của Chat
        Map<String, String> data = new HashMap<>();
        data.put("type", "CHAT");
        data.put("conversationId", conversationId);

        // 3. Ra lệnh cho FcmService bắn đi
        // Nó sẽ truyền các Fcm_Token để đẩy thông báo trên nhiều máy nếu người dùng xài nhiều
        // Tiều đề - Header
        // Tiêu đề phụ - SubHeader
        // Nội dung - Content
        // Dữ liệu
        // -> Nó chỉ truyền đi content của Message để đẩy thông báo qua bên kia
        fcmService.sendPushNotificationToUser(tokens, "Tin nhắn từ - " + senderName, content, data);
    }
}
