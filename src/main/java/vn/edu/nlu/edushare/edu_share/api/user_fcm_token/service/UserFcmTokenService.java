package vn.edu.nlu.edushare.edu_share.api.user_fcm_token.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;
import vn.edu.nlu.edushare.edu_share.api.user.repository.UserRepository;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.model.UserFcmToken;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.repository.UserFcmTokenRepository;

import java.util.List;
import java.util.Optional;

//(Người Thủ thư): Chỉ làm đúng một việc là quản lý "Danh bạ" (Database MySQL).
// Nó nhận token từ Android gửi lên, kiểm tra xem máy mới hay máy cũ, ghi đè hay tạo mới.
// Nó không hề biết Google Firebase là cái gì.
@Service
public class UserFcmTokenService {

    @Autowired
    private UserFcmTokenRepository userFcmTokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveOrUpdateFcmToken(String userId, String token) {
        // 1. Kiểm tra xem cái Token (thiết bị) này đã từng được lưu chưa
        List<UserFcmToken> existingTokens = userFcmTokenRepository.findByFcmToken(token);

        // Tìm đối tượng User mới từ Database bằng userId
        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID này"));

        if (!existingTokens.isEmpty()) {
            UserFcmToken existingToken = existingTokens.get(0);

            // Nếu token đã có nhưng đang gắn với USER KHÁC (Hành động: Người khác vừa đăng nhập vào máy này)
            if (!existingToken.getUser().getId().equals(userId)) {

                // 2. Cập nhật lại chủ sở hữu mới cho thiết bị này (Truyền CẢ OBJECT USER vào)
                existingToken.setUser(newUser);

                // 3. Lưu xuống Database
                userFcmTokenRepository.save(existingToken);
            }

            // Nếu có nhiều hơn 1 token trùng lặp, xóa các token trùng lặp còn lại để dọn dẹp DB
            if (existingTokens.size() > 1) {
                for (int i = 1; i < existingTokens.size(); i++) {
                    userFcmTokenRepository.delete(existingTokens.get(i));
                }
            }
        } else {
            // 2. Nếu là thiết bị hoàn toàn mới chat lần đầu -> Khởi tạo dòng mới
            UserFcmToken newToken = new UserFcmToken();
            newToken.setUser(newUser);
            newToken.setFcmToken(token);
            userFcmTokenRepository.save(newToken);
        }
    }
}
