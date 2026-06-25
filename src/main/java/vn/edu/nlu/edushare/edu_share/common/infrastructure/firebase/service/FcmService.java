package vn.edu.nlu.edushare.edu_share.common.infrastructure.firebase.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.Message;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.model.UserFcmToken;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.repository.UserFcmTokenRepository;

import java.util.List;
import java.util.Map;

//(Người Bưu tá): Chỉ làm đúng một việc là "Giao hàng".
// Nó nhận nội dung tin nhắn và danh sách Token, đóng gói lại và gọi API ném sang cho Google.
// Nó không hề biết Database MySQL lưu trữ bảng nào, khóa chính khóa ngoại ra sao.
@Service
@RequiredArgsConstructor
public class FcmService {

    private final UserFcmTokenRepository userFcmTokenRepository;

    /**
     * Hàm gửi thông báo đến một danh sách Token thiết bị (Gửi đồng loạt)
     */
    public void sendPushNotificationToUser(List<String> tokens, String title, String body, Map<String, String> dataPayload) {
        // 1. Lấy TẤT CẢ các token/thiết bị mà người nhận đang đăng nhập
        if (tokens == null || tokens.isEmpty()) return;

        for (String token : tokens) {
            try {
                Message.Builder messageBuilder = Message.builder().setToken(token).setNotification(
                        Notification.builder().setTitle(title).setBody(body).build()
                );

                // Nếu có truyền dữ liệu ngầm kèm theo thì nhét vào gói tin
                if (dataPayload != null && !dataPayload.isEmpty()) {
                    messageBuilder.putAllData(dataPayload);
                }

                // Bắn bất đồng bộ sang Google
                FirebaseMessaging.getInstance().sendAsync(messageBuilder.build());

            } catch (Exception e) {
                System.err.println("Lỗi gửi FCM đến token: " + token + " - " + e.getMessage());
            }
        }
    }
}
