package vn.edu.nlu.edushare.edu_share.api.transaction.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.edu.nlu.edushare.edu_share.api.article.model.Post;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;

import java.sql.Timestamp;

@Entity
@Table(name = "transactions") // Trùng khớp với tên bảng trong Database
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type; //

    // Trạng thái của giao dịch (PENDING, IN_PROGRESS, SUCCESS,...) lưu dạng String trong DB
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status; //

    // Tự động sinh thời gian khi tạo bản ghi mới (Cột created_at timestamp)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt; //

    // Tự động cập nhật thời gian mỗi khi bản ghi được update (Cột updated_at timestamp)
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt; //

    public enum TransactionStatus {
        PENDING,
        IN_PROGRESS,
        SUCCESS,
        REJECTED,
        CANCELED
    }

    public enum TransactionType {
        FREE,
        SALE,
        EXCHANGE
    }


}



