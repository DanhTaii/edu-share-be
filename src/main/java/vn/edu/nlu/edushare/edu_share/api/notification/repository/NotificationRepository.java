package vn.edu.nlu.edushare.edu_share.api.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
    List<NotificationResponseProjection> findNotificationsWithPostImage(@Param("userId") String userId);

    @Query("SELECT n.id AS id, n.title AS title, n.content AS content, " +
            "n.type AS type, n.referenceId AS referenceId, n.isRead AS isRead, n.createdAt AS createdAt, " +
            "p.imageUrl AS avatarUrl " +
            "FROM Notification n LEFT JOIN Post p ON n.referenceId = p.id " +
            "WHERE n.id = :notificationId")
    NotificationResponseProjection findSingleNotificationWithImage(@Param("notificationId") Integer notificationId);
}
