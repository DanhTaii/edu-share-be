package vn.edu.nlu.edushare.edu_share.api.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Integer id;
    private Integer postId;
    private String postTitle;
    private String postImage;
    private String buyerId;
    private String sellerId;
    private String transactionType; // SALE, EXCHANGE, FREE
    private String status;
}
