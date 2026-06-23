package vn.edu.nlu.edushare.edu_share.api.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostDetailResponseDTO;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.model.Post;

import java.util.List;

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

    @Query(value = """
            SELECT
                p.id AS id,
                p.title AS title,
                p.description AS description,
                p.price AS price,
                p.image_url AS imageUrl,
                p.status AS status,
                p.category_id AS categoryId,
                c.name AS categoryName,
                p.location_id AS locationId,
                l.area_name AS locationName,
                p.author_id AS authorId,
                u.full_name AS authorName
            FROM posts p
            LEFT JOIN categories c ON c.id = p.category_id
            LEFT JOIN locations l ON l.id = p.location_id
            LEFT JOIN users u ON u.id = p.author_id
            WHERE p.status <> 'HIDDEN'
            ORDER BY p.created_at DESC, p.id DESC
            """, nativeQuery = true)
    List<PostListItemProjection> findVisiblePostList();

    @Query(value = """
        SELECT new vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostDetailResponseDTO(
            p.id, p.title, p.description, p.price, p.imageUrl, p.status, p.transactionType, 
            c.id, c.name, l.id, l.latitude, l.longitude, l.areaName, u.id, u.fullName
        )
        FROM Post p
        LEFT JOIN p.category c
        LEFT JOIN p.location l
        LEFT JOIN p.author u
        WHERE p.id = :postId
        """)
    PostDetailResponseDTO findPostDetail(@Param("postId") Integer postId);
    }
