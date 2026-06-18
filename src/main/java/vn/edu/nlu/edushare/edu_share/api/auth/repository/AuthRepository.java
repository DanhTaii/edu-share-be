package vn.edu.nlu.edushare.edu_share.api.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    boolean existsByStudentCode(String studentCode);

    Optional<User> findByEmail(String email);
}
