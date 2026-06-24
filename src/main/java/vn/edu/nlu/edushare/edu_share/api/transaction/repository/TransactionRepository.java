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
    }
