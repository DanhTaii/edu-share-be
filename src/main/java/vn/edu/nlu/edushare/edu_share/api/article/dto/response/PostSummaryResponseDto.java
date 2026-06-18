package vn.edu.nlu.edushare.edu_share.api.article.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryResponseDto {
    private int id;
    private String title;
    private double price;
    private String imageUrl;

}
