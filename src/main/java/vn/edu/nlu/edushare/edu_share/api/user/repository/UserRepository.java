package vn.edu.nlu.edushare.edu_share.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByStudentCode(String studentCode);

    // Tìm người dùng theo Email (Dùng cho đăng nhập/kiểm tra trùng)
    Optional<User> findByEmail(String email);

    // Kiểm tra tồn tại trước khi đăng ký
    boolean existsByEmail(String email);

    boolean existsByStudentCode(String studentCode);
}
