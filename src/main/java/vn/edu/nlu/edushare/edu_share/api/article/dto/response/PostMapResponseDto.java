package vn.edu.nlu.edushare.edu_share.api.article.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMapResponseDto {
    private Integer id;
    private String title;
    private String description;
    private String imageUrl;
    private Double price;
    private String status;
    private String areaName;    // tên khu vực (Dorm A, Library,...)
    private Double latitude;
    private Double longitude;
}