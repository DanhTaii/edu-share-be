package vn.edu.nlu.edushare.edu_share.api.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT new vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageResponseDto(" +
            "m.id, m.conversation.id, m.senderId, m.content, m.isRead, m.createdAt) " +
            "FROM Message m " +
            "WHERE m.conversation.id = :conversationId " +
            "ORDER BY m.createdAt ASC")
    List<MessageResponseDto> findByConversationIdOrderByCreatedAtAsc(@Param("conversationId") Integer conversationId);
}
