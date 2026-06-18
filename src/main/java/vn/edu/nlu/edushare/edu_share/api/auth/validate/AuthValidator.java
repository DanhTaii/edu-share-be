package vn.edu.nlu.edushare.edu_share.api.auth.validate;

import org.springframework.stereotype.Component;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.request.RegisterRequest;

@Component
public class AuthValidator {

    public void validateRegister(RegisterRequest request) {

        String fullName = request.getFullName();
        String email = request.getEmail();
        String studentCode = request.getStudentCode();
        String phone = request.getPhone();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        // Full Name
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Không được để trống họ tên");
        }

        if (fullName.trim().length() < 2) {
            throw new IllegalArgumentException("Tên quá ngắn");
        }

        // Email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }

        // Student Code
        if (studentCode == null || studentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã số sinh viên không được để trống");
        }

        // Phone
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        if (!phone.matches("\\d{10}")) {
            throw new IllegalArgumentException("Số điện thoại phải gồm 10 chữ số");
        }

        // Password
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu tối thiểu 6 ký tự");
        }

        // Confirm Password
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            throw new IllegalArgumentException("Mật khẩu không khớp");
        }
    }
}