package vn.edu.nlu.edushare.edu_share.api.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Integer conversationId);
}
