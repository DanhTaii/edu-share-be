package vn.edu.nlu.edushare.edu_share.api.auth.dto.request;

import lombok.Data;

@Data
public class OcrLoginRequest {
    private String studentCode;
    private String email;
    private String fullName;
}
