package vn.edu.nlu.edushare.edu_share.api.user_fcm_token.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.Message;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.model.UserFcmToken;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.repository.UserFcmTokenRepository;

import java.util.List;

//(Người Bưu tá): Chỉ làm đúng một việc là "Giao hàng".
// Nó nhận nội dung tin nhắn và danh sách Token, đóng gói lại và gọi API ném sang cho Google.
// Nó không hề biết Database MySQL lưu trữ bảng nào, khóa chính khóa ngoại ra sao.
@Service
@RequiredArgsConstructor
public class FcmService {

    private final UserFcmTokenRepository userFcmTokenRepository;

    public void sendPushNotificationToUser(String recipientId, String title, String body, String conversationId) {
        // 1. Lấy TẤT CẢ các token/thiết bị mà người nhận đang đăng nhập
        List<UserFcmToken> userTokens = userFcmTokenRepository.findByUserId(recipientId);

        if (userTokens == null || userTokens.isEmpty()) {
            System.out.println("Người dùng " + recipientId + " không có thiết bị. Bỏ qua gửi FCM.");
            return;
        }

        // 2. Duyệt qua từng thiết bị để phát thông báo đồng loạt
        for (UserFcmToken device : userTokens) {
            try {
                // Gửi đích danh đến thiết bị này
                Message message = Message.builder().setToken(device.getFcmToken()).setNotification(
                        Notification.builder().setTitle(title).setBody(body).build())
                        .putData("conversationId", conversationId)
                        .build();

                // Bắn đi
                FirebaseMessaging.getInstance().sendAsync(message);
                // Dùng sendAsync thay vì send để Server không bị nghẽn (Block)
                // khi người dùng có quá nhiều thiết bị, giúp tốc độ phản hồi API chat nhanh hơn.

            } catch (Exception e) {
                System.err.println("Lỗi gửi FCM cho token " + device.getFcmToken() + ": " + e.getMessage());
            }
        }
    }
}
