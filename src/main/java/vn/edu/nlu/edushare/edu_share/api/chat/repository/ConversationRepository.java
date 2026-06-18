package vn.edu.nlu.edushare.edu_share.api.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.response.ConversationResponseDto;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Conversation;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    @Query("""
                SELECT new vn.edu.nlu.edushare.edu_share.api.chat.dto.response.ConversationResponseDto(
                    c.id,
                    c.postId,
                    CASE WHEN c.userOne.id = :userId THEN c.userTwo.id ELSE c.userOne.id END,
                    CASE WHEN c.userOne.id = :userId THEN c.userTwo.fullName ELSE c.userOne.fullName END,
                    CASE WHEN c.userOne.id = :userId THEN c.userTwo.avatarUrl ELSE c.userOne.avatarUrl END,
                    c.lastMessage,
                    c.updatedAt,
                    (
                        SELECT COUNT(m)
                        FROM Message m
                        WHERE m.conversation.id = c.id
                        AND m.isRead = false
                        AND m.senderId <> :userId
                    )
                )
                FROM Conversation c
                WHERE c.userOne.id = :userId OR c.userTwo.id = :userId
                ORDER BY c.updatedAt DESC
            """)
    Page<ConversationResponseDto> findUserConversations(@Param("userId") String userId, Pageable pageable);
}
