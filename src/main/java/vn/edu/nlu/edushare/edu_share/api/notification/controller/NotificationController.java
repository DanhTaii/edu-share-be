package vn.edu.nlu.edushare.edu_share.api.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.nlu.edushare.edu_share.api.notification.dto.request.NotificationRequestDto;
import vn.edu.nlu.edushare.edu_share.api.notification.dto.response.NotificationResponseProjection;
import vn.edu.nlu.edushare.edu_share.api.notification.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping()
    public NotificationResponseProjection sendNotification(@RequestBody NotificationRequestDto notificationRequestDto) {
        // Gọi service để gửi thông báo
        return notificationService.sendTransactionNotification(
                notificationRequestDto.getRecipientId(),
                notificationRequestDto.getSenderName(),
                notificationRequestDto.getItemName(),
                notificationRequestDto.getPostId()
        );
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponseProjection>> getUserNotifications(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // Lấy danh sách thông báo của user này, sắp xếp mới nhất lên đầu
        Page<NotificationResponseProjection> notifications = notificationService.getNotificationByUserId(userId, page, size);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@RequestParam String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-unread")
    public ResponseEntity<Boolean> checkUnread(@RequestParam String userId) {
        boolean hasUnread = notificationService.checkUnreadNotification(userId);
        return ResponseEntity.ok(hasUnread);
    }


}
