package vn.edu.nlu.edushare.edu_share.api.mail.request;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String fullName;
    private String email;
    private String phone;
    private String studentCode;
    private String password;
    private String otp;
}
