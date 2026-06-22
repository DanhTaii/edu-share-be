package vn.edu.nlu.edushare.edu_share.api.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.request.LoginRequest;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.request.OcrLoginRequest;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.request.RegisterRequest;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.response.LoginResponse;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.response.RegisterResponse;
import vn.edu.nlu.edushare.edu_share.api.auth.repository.AuthRepository;
import vn.edu.nlu.edushare.edu_share.api.auth.validate.AuthValidator;
import vn.edu.nlu.edushare.edu_share.api.mail.model.EmailVerification;
import vn.edu.nlu.edushare.edu_share.api.mail.repository.EmailVerificationRepository;
import vn.edu.nlu.edushare.edu_share.api.mail.request.SendOtpRequest;
import vn.edu.nlu.edushare.edu_share.api.mail.service.EmailService;
import vn.edu.nlu.edushare.edu_share.api.mail.util.OtpGenerator;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;
import vn.edu.nlu.edushare.edu_share.api.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;

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

        return RegisterResponse.builder().success(true).message("Đăng ký thành công").build();
    }

    public String sendOtp(SendOtpRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        emailVerificationRepository.findTopByEmailOrderByExpiredAtDesc(request.getEmail())
                .ifPresent(emailVerificationRepository::delete);

        String otp = OtpGenerator.generateOtp();

        EmailVerification verification = new EmailVerification();
        verification.setEmail(request.getEmail());
        verification.setOtpCode(otp);
        verification.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        emailVerificationRepository.save(verification);
        emailService.sendOtp(request.getEmail(), otp);

        return "Đã gửi mã OTP: " + otp;
    }

    public LoginResponse login(LoginRequest request) {

        authValidator.validateLogin(request);

        User user = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai mật khẩu");
        }

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .success(true)
                .message("Đăng nhập thành công")
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }

    public LoginResponse ocrLogin(OcrLoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Không đọc được email từ OCR");
        }

        User user = userRepository.findByStudentCode(request.getStudentCode())
                .orElseGet(() -> {
                    User newUser =
                            User.builder()
                                    .id(UUID.randomUUID().toString())
                                    .fullName(request.getFullName())
                                    .email(request.getEmail())
                                    .studentCode(request.getStudentCode())
                                    .phone("")
                                    .role(User.Role.STUDENT)
                                    .isVerified(false)
                                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                    .build();
                    return userRepository.save(newUser);
                });

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .success(true)
                .message("OCR login thành công")
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }

}
