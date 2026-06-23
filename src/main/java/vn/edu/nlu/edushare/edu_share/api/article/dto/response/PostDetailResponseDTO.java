package vn.edu.nlu.edushare.edu_share.api.article.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.nlu.edushare.edu_share.api.article.model.Post;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private Double price;
    private String imageUrl;
    private Post.Status status;
    private Post.TransactionType transactionType;
    private Integer categoryId;
    private String categoryName;
    private Integer locationId;
    private Double latitude;
    private Double longitude;
    private String locationName;
    private String authorId;
    private String authorName;
}
