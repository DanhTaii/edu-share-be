package vn.edu.nlu.edushare.edu_share.api.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String id; // Firebase UID

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    @JsonProperty("student_code")
    private String studentCode;

    private String avatarUrl;

}
