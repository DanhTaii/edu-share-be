package vn.edu.nlu.edushare.edu_share.api.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.notification.dto.response.NotificationResponseProjection;
import vn.edu.nlu.edushare.edu_share.api.notification.model.Notification;
import vn.edu.nlu.edushare.edu_share.api.notification.repository.NotificationRepository;
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
    private final FcmService fcmService;
    private final NotificationRepository notificationRepository;

    /**
     * Xử lý cho tính năng CHAT
     */
    public void sendChatNotification(String recipientId, String senderName, String content, String conversationId) {
        // 1. Lấy danh sách token từ DB chuyển thành list chuỗi String thuần
        List<String> tokens = tokenRepository.findByUserId(recipientId).stream().map(UserFcmToken::getFcmToken).toList();

        // 2. Chuẩn bị data đặc trưng của Chat
        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("type", "CHAT");
        dataPayload.put("conversationId", conversationId);

        // 3. Ra lệnh cho FcmService bắn đi
        // Nó sẽ truyền các Fcm_Token để đẩy thông báo trên nhiều máy nếu người dùng xài nhiều
        // Tiều đề - Header
        // Tiêu đề phụ - SubHeader
        // Nội dung - Content
        // Dữ liệu
        // -> Nó chỉ truyền đi content của Message để đẩy thông báo qua bên kia
        fcmService.sendPushNotificationToUser(tokens, "Tin nhắn từ - " + senderName, content, dataPayload);
    }

    /**
     * Xử lý cho tính năng TRANSACTION
     */
    public NotificationResponseProjection sendTransactionNotification(String recipientId, String senderName, String itemName, Integer postId) {
        // TẠO ĐỐI TƯỢNG LƯU VÀO DB
        Notification dbNotification = new Notification();
        dbNotification.setUserId(recipientId);
        dbNotification.setTitle("Yêu cầu mượn đồ");
        dbNotification.setContent(senderName + " muốn mượn " + itemName + " của bạn.");
        dbNotification.setType("TRANSACTION");
        dbNotification.setReferenceId(postId);
        // LƯU VÀO DB
        notificationRepository.save(dbNotification);

        // 2. Lấy FCM Token từ DB để thông báo như CHAT ở trên
        List<String> tokens = tokenRepository.findByUserId(recipientId).stream().map(UserFcmToken::getFcmToken).toList();

        // Chuẩn bị GỬI DATA sang cho Firebase Server
        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("type", "TRANSACTION");
        dataPayload.put("referenceId", String.valueOf(postId));
        dataPayload.put("notificationId", String.valueOf(dbNotification.getId())); // Truyền ID thông báo nếu Mobile cần đổi màu đã đọc

        fcmService.sendPushNotificationToUser(tokens, dbNotification.getTitle(), dbNotification.getContent(), dataPayload);

        // Trả về thông báo vừa tạo để Mobile có thể hiển thị
        return notificationRepository.findSingleNotificationWithImage(dbNotification.getId());
    }

    public Page<NotificationResponseProjection> getNotificationByUserId(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findNotificationsWithPostImage(userId, pageable);
    }

    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsRead(userId);
    }

    public boolean checkUnreadNotification(String userId) {
        return notificationRepository.existsByUserIdAndIsReadFalse(userId);
    }

}
