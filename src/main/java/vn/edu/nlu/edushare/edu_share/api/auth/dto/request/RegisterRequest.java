package vn.edu.nlu.edushare.edu_share.api.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest implements Serializable {

    private String fullName;

    private String email;

    private String phone;

    private String studentCode;

    private String password;

    private String confirmPassword;
}
