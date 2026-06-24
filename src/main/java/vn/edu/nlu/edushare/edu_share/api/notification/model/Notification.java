package vn.edu.nlu.edushare.edu_share.api.notification.model;

import jakarta.persistence.*;
import lombok.Data;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ID của người nhận thông báo
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    // Mặc định tạo ra là chưa đọc (false)
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    // Rất quan trọng để Android biết mở Fragment/Activity nào
    // Thường dùng các chữ: "POST", "TRANSACTION", "SYSTEM"
    @Column(nullable = false, length = 50)
    private String type;

    // Mã của món đồ hoặc mã giao dịch để chèn vào Intent chuyển màn hình
    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    //Tự động set thời gian lúc bạn gọi hàm save()
    @PrePersist
    protected void onCreate() {
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
    }
}
