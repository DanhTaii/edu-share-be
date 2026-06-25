package vn.edu.nlu.edushare.edu_share.api.article.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequestDto {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    private String description;

    private String condition;

    @PositiveOrZero(message = "Price must be greater than or equal to 0")
    private Double price;

    private String imageUrl;

    private Integer categoryId;

    private String categoryName;

    private Integer locationId;

    private String itemType;

    private String transactionType;
}