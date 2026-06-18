package vn.edu.nlu.edushare.edu_share.api.chat.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT new vn.edu.nlu.edushare.edu_share.api.chat.dto.response.MessageResponseDto(" +
            "m.id, m.conversation.id, m.senderId, m.content, m.isRead, m.createdAt) " +
            "FROM Message m " +
            "WHERE m.conversation.id = :conversationId " +
            "ORDER BY m.createdAt DESC")
    Page<MessageResponseDto> findByConversationIdOrderByCreatedAtAsc(@Param("conversationId") Integer conversationId, Pageable pageable);

    // Thường xuyên sử dụng @Modifying khi bạn muốn thực hiện các thao tác thay đổi dữ liệu
    // (như INSERT, UPDATE, DELETE) trong cơ sở dữ liệu thông qua JPA.
    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true " +
            "WHERE m.conversation.id = :conversationId " +
            // Chỉ đánh dấu là đã đọc nếu người gửi không phải là người hiện tại
            "AND m.senderId <> :userId  " +
            //và tin nhắn chưa được đọc
            "AND m.isRead = false")
    int markMessagesAsRead(@Param("conversationId") Integer conversationId, @Param("userId") String userId);
}
