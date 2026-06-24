package vn.edu.nlu.edushare.edu_share.api.notification.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.nlu.edushare.edu_share.api.notification.dto.response.NotificationResponseProjection;
import vn.edu.nlu.edushare.edu_share.api.notification.model.Notification;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("SELECT n.id AS id, n.title AS title, n.content AS content, " +
            "n.type AS type, n.referenceId AS referenceId, n.isRead AS isRead, n.createdAt AS createdAt, " +
            "p.imageUrl AS avatarUrl " +
            "FROM Notification n LEFT JOIN Post p ON n.referenceId = p.id " +
            "WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    Page<NotificationResponseProjection> findNotificationsWithPostImage(@Param("userId") String userId, Pageable page);

    @Query("SELECT n.id AS id, n.title AS title, n.content AS content, " +
            "n.type AS type, n.referenceId AS referenceId, n.isRead AS isRead, n.createdAt AS createdAt, " +
            "p.imageUrl AS avatarUrl " +
            "FROM Notification n LEFT JOIN Post p ON n.referenceId = p.id " +
            "WHERE n.id = :notificationId")
    NotificationResponseProjection findSingleNotificationWithImage(@Param("notificationId") Integer notificationId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") String userId);

    boolean existsByUserIdAndIsReadFalse(String userId);

}
