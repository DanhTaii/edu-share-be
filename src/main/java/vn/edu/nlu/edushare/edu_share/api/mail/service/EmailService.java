package vn.edu.nlu.edushare.edu_share.api.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Mã xác thực EduShare");

        message.setText("Xin chào,\n\n" +
                        "Mã xác thực của bạn là: " + otp +
                        "\n\nMã có hiệu lực trong 5 phút."
        );

        mailSender.send(message);
    }
}
