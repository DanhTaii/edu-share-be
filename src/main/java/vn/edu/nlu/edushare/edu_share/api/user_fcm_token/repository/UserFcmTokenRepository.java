package vn.edu.nlu.edushare.edu_share.api.user_fcm_token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.model.UserFcmToken;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Integer> {
    // Tìm tất cả các máy/token của 1 người dùng để bắn thông báo đồng loạt
    List<UserFcmToken> findByUserId(String userId);

    // Tìm xem cái token này đã từng tồn tại dưới DB chưa
    List<UserFcmToken> findByFcmToken(String fcmToken);
}
