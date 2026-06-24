package vn.edu.nlu.edushare.edu_share.api.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostDetailResponseDTO;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.model.Post;
import vn.edu.nlu.edushare.edu_share.api.article.repository.PostListItemProjection;
import vn.edu.nlu.edushare.edu_share.api.transaction.model.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    boolean existsByPostIdAndBuyerIdAndStatus(Integer postId, String buyerId, Transaction.TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:role = 'BUYER' AND t.buyerId = :userId AND t.status IN :statuses) OR " +
            "(:role = 'SELLER' AND t.sellerId = :userId AND t.status IN :statuses) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findHistory(
            @Param("userId") String userId,
            @Param("role") String role,
            @Param("statuses") List<Transaction.TransactionStatus> statuses // Nhận vào một List thay vì 1 String
    );
    }
