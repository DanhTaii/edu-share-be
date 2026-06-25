package vn.edu.nlu.edushare.edu_share.common.infrastructure.firebase.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

//Chạy 1 lần duy nhất khi Server khởi động để thiết lập đường truyền an toàn với Google.
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() throws IOException {
        try {
            // Đọc file JSON từ thư mục resources
            InputStream serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Khởi tạo Firebase nếu chưa có
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK đã khởi tạo thành công!");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo Firebase: " + e.getMessage());
        }
    }
}