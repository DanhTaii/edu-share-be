package vn.edu.nlu.edushare.edu_share.api.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostListItemResponseDto;
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
}
