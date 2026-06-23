package vn.edu.nlu.edushare.edu_share.api.article.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.nlu.edushare.edu_share.api.article.model.Post;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // Tạo constructor nhận đúng thứ tự các trường bên dưới
public class PostDetailResponseDTO {
    // 1. Thông tin Post
    private Integer id;
    private String title;
    private String description;
    private Double price;
    private String imageUrl;
    private Post.Status status;
    private Post.TransactionType transactionType;

    // 2. Thông tin Category
    private Integer categoryId;
    private String categoryName;

    // 3. Thông tin Location
    private Integer locationId;
    private Double latitude;
    private Double longitude;
    private String locationName;

    // 4. Thông tin Author (User)
    private String authorId;
    private String authorName;
    private String avatarUrl;
    private boolean isVerified;
}