package vn.edu.nlu.edushare.edu_share.api.chat.model;

import jakarta.persistence.*;
import lombok.Data;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Data
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "user_one_id", referencedColumnName = "id")
    private User userOne;

    @ManyToOne
    @JoinColumn(name = "user_two_id", referencedColumnName = "id")
    private User userTwo;

    private String lastMessage;

    private LocalDateTime updatedAt;
}
