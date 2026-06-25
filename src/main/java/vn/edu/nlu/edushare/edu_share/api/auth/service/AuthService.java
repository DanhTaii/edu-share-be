package vn.edu.nlu.edushare.edu_share.api.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.request.*;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.response.*;
import vn.edu.nlu.edushare.edu_share.api.auth.repository.AuthRepository;
import vn.edu.nlu.edushare.edu_share.api.auth.validate.AuthValidator;
import vn.edu.nlu.edushare.edu_share.api.mail.model.EmailVerification;
import vn.edu.nlu.edushare.edu_share.api.mail.repository.EmailVerificationRepository;
import vn.edu.nlu.edushare.edu_share.api.mail.request.SendOtpRequest;
import vn.edu.nlu.edushare.edu_share.api.mail.service.EmailService;
import vn.edu.nlu.edushare.edu_share.common.utils.OtpGenerator;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;
import vn.edu.nlu.edushare.edu_share.api.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final AuthRepository authRepository;

    public RegisterResponse sendOtp(SendOtpRequest request) {

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

        return RegisterResponse.builder().success(true).message("Đã gửi mã OTP thành công").build();
    }

    public RegisterResponse register(RegisterRequest request) {
        authValidator.validateRegister(request);

        EmailVerification verification = emailVerificationRepository.findTopByEmailOrderByExpiredAtDesc(request.getEmail())
                .orElseThrow(() -> new RuntimeException("OTP không tồn tại hoặc đã bị hủy"));

        if (verification.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        if (!verification.getOtpCode().equalsIgnoreCase(request.getOtp())) {
            throw new RuntimeException("Mã OTP nhập vào không chính xác");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .studentCode(request.getStudentCode())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.STUDENT)
                .isVerified(true)
                .build();

        try {
            // Dùng saveAndFlush để ép Hibernate đẩy dữ liệu xuống DB ngay lập tức tại đây
            userRepository.saveAndFlush(newUser);
            emailVerificationRepository.delete(verification);
        } catch (Exception e) {
            // Bắt Exception chung để đảm bảo tóm gọn mọi lỗi JPA/Hibernate nổ ra khi Flush
            System.out.println("-> Phát hiện xung đột luồng tại Register. Đang kiểm tra lại DB...");
            if (userRepository.existsByEmail(request.getEmail())) {
                return RegisterResponse.builder().success(true).message("Đăng ký tài khoản thành công!").build();
            }
            throw e; // Nếu là lỗi khác thì vẫn bắn ra ngoài
        }

        return RegisterResponse.builder()
                .success(true)
                .message("Đăng ký tài khoản thành công!")
                .build();
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

        java.util.Optional<User> existingUser = userRepository.findByStudentCode(request.getStudentCode());
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            User newUser = User.builder()
                    .id(UUID.randomUUID().toString())
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .studentCode(request.getStudentCode())
                    .phone("")
                    .role(User.Role.STUDENT)
                    .isVerified(false)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();
            try {
                // Ép ghi xuống DB ngay tại đây để bọc catch hoạt động chính xác
                user = userRepository.saveAndFlush(newUser);
            } catch (Exception e) {
                System.out.println("-> Phát hiện xung đột chạy đua luồng tại ocrLogin. Đang lấy lại dữ liệu...");
                user = userRepository.findByStudentCode(request.getStudentCode())
                        .orElseThrow(() -> new RuntimeException("Lỗi đồng bộ dữ liệu người dùng"));
            }
        }

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