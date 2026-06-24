package vn.edu.nlu.edushare.edu_share.api.article.dto.request;

import lombok.Data;

@Data
public class CreateLocationRequestDto {
    private String areaName;
    private Double latitude;
    private Double longitude;
}