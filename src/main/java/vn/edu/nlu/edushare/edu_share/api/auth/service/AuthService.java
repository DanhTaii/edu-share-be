package vn.edu.nlu.edushare.edu_share.api.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.request.RegisterRequest;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.response.RegisterResponse;
import vn.edu.nlu.edushare.edu_share.api.auth.repository.AuthRepository;
import vn.edu.nlu.edushare.edu_share.api.auth.validate.AuthValidator;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthValidator authValidator;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {

        authValidator.validateRegister(request);

        if (authRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (authRepository.existsByStudentCode(request.getStudentCode())) {
            throw new RuntimeException("Mã số sinh viên đã tồn tại");
        }

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .studentCode(request.getStudentCode())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.STUDENT)
                .isVerified(false)
                .build();

        authRepository.save(user);

        return RegisterResponse.builder()
                .success(true)
                .message("Đăng ký thành công")
                .build();
    }
}
