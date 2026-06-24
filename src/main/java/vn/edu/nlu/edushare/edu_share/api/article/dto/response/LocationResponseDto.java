package vn.edu.nlu.edushare.edu_share.api.article.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDto {
    private Integer id;
    private String areaName;
    private Double latitude;
    private Double longitude;
}