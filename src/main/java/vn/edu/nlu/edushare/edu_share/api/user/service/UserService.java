package vn.edu.nlu.edushare.edu_share.api.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.auth.service.JwtService;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;
import vn.edu.nlu.edushare.edu_share.api.user.repository.UserRepository;
import vn.edu.nlu.edushare.edu_share.api.user.request.UserRegistrationRequest;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    public User registerUser(UserRegistrationRequest request) {
        // Kiểm tra xem ID (Firebase UID) đã tồn tại trong DB chưa
        if (userRepository.existsById(request.getId())) {
            throw new RuntimeException("User đã tồn tại trong hệ thống!");
        }

        // Map từ DTO sang Entity
        User newUser = new User();
        newUser.setId(request.getId());
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setStudentCode(request.getStudentCode());
        newUser.setAvatarUrl(request.getAvatarUrl());
        // Các trường như role, isVerified đã có giá trị mặc định ở Entity

        return userRepository.save(newUser);
    }

    // CREATE: Tạo mới người dùng
    public User createUser(User user) {
        if (userRepository.existsById(user.getId())) {
            throw new RuntimeException("Lỗi: Firebase UID đã tồn tại!");
        }
        return userRepository.save(user);
    }

    // READ: Lấy danh sách tất cả (cho Admin)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // READ: Lấy chi tiết 1 người theo ID
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
    }

    // UPDATE: Cập nhật thông tin (Avatar, Tên, Token thông báo)
    public User updateUser(String id, User userDetails) {
        User user = getUserById(id); // Tận dụng hàm Read ở trên

        user.setFullName(userDetails.getFullName());
        user.setAvatarUrl(userDetails.getAvatarUrl());
//        user.setFcmToken(userDetails.getFcmToken());
        user.setVerified(userDetails.isVerified());

        return userRepository.save(user);
    }

    // DELETE: Xóa tài khoản
    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User getCurrentUser(String token) {
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }
}
