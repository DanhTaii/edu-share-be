package vn.edu.nlu.edushare.edu_share.api.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.nlu.edushare.edu_share.api.mail.model.EmailVerification;

import java.util.Optional;

public interface EmailVerificationRepository
        extends JpaRepository<EmailVerification, String> {

    Optional<EmailVerification>
    findTopByEmailOrderByExpiredAtDesc(String email);
}