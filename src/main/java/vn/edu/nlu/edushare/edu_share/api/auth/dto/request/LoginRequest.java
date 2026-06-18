package vn.edu.nlu.edushare.edu_share.api.auth.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
