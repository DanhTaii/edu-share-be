package vn.edu.nlu.edushare.edu_share.api.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.model.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("""
            SELECT new vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto(
                    p.id,
                    p.title,
                    p.price,
                    p.imageUrl
                )
                FROM Post p
                WHERE p.id = :postId
            """)
    PostSummaryResponseDto findPostSummaryById(@Param("postId") Integer postId);

}
