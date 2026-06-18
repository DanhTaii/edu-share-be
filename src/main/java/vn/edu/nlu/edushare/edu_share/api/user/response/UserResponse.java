package vn.edu.nlu.edushare.edu_share.api.user.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String studentCode;
    private String role;
}
