package vn.edu.nlu.edushare.edu_share.api.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostDetailResponseDTO;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostListItemResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostMapResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.repository.PostListItemProjection;
import vn.edu.nlu.edushare.edu_share.api.article.repository.PostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<PostListItemResponseDto> getPosts() {
        return postRepository.findVisiblePostList()
                .stream()
                .map(this::toPostListItem)
                .toList();
    }

    public PostDetailResponseDTO getDetailPost(Integer postId) {
        return postRepository.findPostDetail(postId);
    }

    public PostSummaryResponseDto getPostSummaryById(Integer postId) {
        return postRepository.findPostSummaryById(postId);
    }

    private PostListItemResponseDto toPostListItem(PostListItemProjection post) {
        return PostListItemResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .price(post.getPrice())
                .imageUrl(post.getImageUrl())
                .status(post.getStatus())
                .categoryId(post.getCategoryId())
                .categoryName(post.getCategoryName())
                .locationId(post.getLocationId())
                .locationName(post.getLocationName())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthorName())
                .build();
    }
    //
    // Thêm vào cuối class, trước dấu }
    public List<PostMapResponseDto> getPostsForMap(String area, String keyword) {
        String areaParam    = (area    != null && !area.isBlank())    ? area    : null;
        String keywordParam = (keyword != null && !keyword.isBlank()) ? keyword : null;

        return postRepository.findPostsForMap(areaParam, keywordParam)
                .stream()
                .map(p -> PostMapResponseDto.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .imageUrl(p.getImageUrl())
                        .price(p.getPrice())
                        .status(p.getStatus())
                        .areaName(p.getAreaName())
                        .latitude(p.getLatitude())
                        .longitude(p.getLongitude())
                        .build())
                .toList();
    }
}
