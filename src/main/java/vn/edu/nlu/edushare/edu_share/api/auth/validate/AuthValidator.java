package vn.edu.nlu.edushare.edu_share.api.auth.validate;

import org.springframework.stereotype.Component;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.request.LoginRequest;
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

        String emailRegex = "^[0-9]{8}@st\\.hcmuaf\\.edu\\.vn$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Email phải có dạng MSSV@st.hcmuaf.edu.vn (VD: 23130192@st.hcmuaf.edu.vn)");
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

        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#_\\-])[A-Za-z\\d@$!%*?&.#_\\-]{8,25}$";

        if (!password.matches(passwordRegex)) {
            throw new IllegalArgumentException(
                    "Mật khẩu phải từ 8 đến 25 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
        }

        // Confirm Password
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            throw new IllegalArgumentException("Mật khẩu không khớp");
        }
    }

    public void validateLogin(LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
    }
}