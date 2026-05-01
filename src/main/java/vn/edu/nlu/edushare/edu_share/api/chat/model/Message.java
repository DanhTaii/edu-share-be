package vn.edu.nlu.edushare.edu_share.api.chat.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation; // Thuộc về cuộc hội thoại nào

    @Column(name = "sender_id", length = 50)
    private String senderId; // Ai là người gửi?
    private String content;  // Nội dung tin nhắn
    private Boolean isRead = false; // Trạng thái đã đọc
    private LocalDateTime createdAt = LocalDateTime.now();
}