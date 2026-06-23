package vn.edu.nlu.edushare.edu_share.api.article.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListItemResponseDto {
    private Integer id;
    private String title;
    private String description;
    private Double price;
    private String imageUrl;
    private String status;
    private Integer categoryId;
    private String categoryName;
    private Integer locationId;
    private String locationName;
    private String authorId;
    private String authorName;
}
