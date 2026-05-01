package vn.edu.nlu.edushare.edu_share.api.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Conversation;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, String> {
    // Tìm cuộc hội thoại dựa trên ID món đồ, và ID của 2 người chat
    Optional<Conversation> findByPostIdAndUserOneIdAndUserTwoId(
            Integer postId, String userOneId, String userTwoId
    );
}
