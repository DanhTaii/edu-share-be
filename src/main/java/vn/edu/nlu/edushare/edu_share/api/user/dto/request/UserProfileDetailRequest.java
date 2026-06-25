package vn.edu.nlu.edushare.edu_share.api.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDetailRequest {
    private String fullName;
    private String studentCode;
    private String phone;
    private String avatarUrl;
}