package vn.edu.nlu.edushare.edu_share.api.chat.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Data
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "post_id")
    private Integer postId; // Gắn với ID bài đăng món đồ

    @Column(name = "user_one_id", length = 50)
    private String userOneId; // Firebase UID người mua

    @Column(name = "user_two_id", length = 50)
    private String userTwoId; // Firebase UID chủ món đồ

    private String lastMessage;

    private LocalDateTime updatedAt;
}
