package vn.edu.nlu.edushare.edu_share.api.mail.request;

import lombok.Data;

@Data
public class SendOtpRequest {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}