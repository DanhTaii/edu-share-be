package vn.edu.nlu.edushare.edu_share.api.user_fcm_token.model;

import jakarta.persistence.*;
import lombok.Data;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;

import java.sql.Timestamp;

@Entity
@Table(name = "user_fcm_tokens")
@Data
public class UserFcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fcm_token", nullable = false, length = 1000)
    private String fcmToken;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
}
