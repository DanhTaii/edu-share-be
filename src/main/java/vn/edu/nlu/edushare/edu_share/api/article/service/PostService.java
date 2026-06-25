package vn.edu.nlu.edushare.edu_share.api.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.edu.nlu.edushare.edu_share.api.article.dto.request.CreatePostRequestDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostDetailResponseDTO;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostListItemResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostMapResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.model.LocationDemo;
import vn.edu.nlu.edushare.edu_share.api.article.model.Post;
import vn.edu.nlu.edushare.edu_share.api.article.repository.LocationRepository;
import vn.edu.nlu.edushare.edu_share.api.article.repository.PostListItemProjection;
import vn.edu.nlu.edushare.edu_share.api.article.repository.PostRepository;
import vn.edu.nlu.edushare.edu_share.api.category.model.Category;
import vn.edu.nlu.edushare.edu_share.api.category.repository.CategoryRepository;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;
import vn.edu.nlu.edushare.edu_share.api.user.repository.UserRepository;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public Page<PostListItemResponseDto> getPosts(Pageable pageable, String category, String keyword) {
        String categoryFilter = normalizeFilter(category);
        String keywordFilter = normalizeFilter(keyword);
        return postRepository.findVisiblePostList(categoryFilter, keywordFilter, pageable)
                .map(this::toPostListItemResponseDto);
    }

    private PostListItemResponseDto toPostListItemResponseDto(PostListItemProjection p) {
        return PostListItemResponseDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .price(p.getPrice())
                .imageUrl(p.getImageUrl())
                .status(p.getStatus())
                .categoryId(p.getCategoryId())
                .categoryName(p.getCategoryName())
                .locationId(p.getLocationId())
                .locationName(p.getLocationName())
                .authorId(p.getAuthorId())
                .authorName(p.getAuthorName())
                .build();
    }

    public PostDetailResponseDTO getDetailPost(Integer postId) {
        return postRepository.findPostDetail(postId);
    }

    public PostSummaryResponseDto getPostSummaryById(Integer postId) {
        return postRepository.findPostSummaryById(postId);
    }

    @Transactional
    public PostListItemResponseDto createPost(CreatePostRequestDto request, String authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = resolveCategory(request);
        LocationDemo location = resolveLocation(request.getLocationId());
        Post.TransactionType transactionType = resolveTransactionType(request);
        Double price = resolvePrice(request.getPrice(), transactionType);

        Post post = Post.builder()
                .author(author)
                .categoryId(category.getId())
                .locationId(location.getId())
                .title(request.getTitle().trim())
                .description(resolveDescription(request))
                .price(price)
                .imageUrl(blankToNull(request.getImageUrl()))
                .status(Post.Status.AVAILABLE)
                .transactionType(transactionType)
                .build();

        Post saved = postRepository.save(post);
        saved.setCategory(category);
        saved.setLocation(location);

        return toPostListItem(saved);
    }
    //
    // Thêm vào cuối class, trước dấu }
    public List<PostMapResponseDto> getPostsForMap(String area, String keyword) {
        String areaParam = (area != null && !area.isBlank()) ? area : null;
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

    private PostListItemResponseDto toPostListItem(Post post) {
        return PostListItemResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .price(post.getPrice())
                .imageUrl(post.getImageUrl())
                .status(post.getStatus().name())
                .categoryId(post.getCategoryId())
                .categoryName(post.getCategory() != null ? post.getCategory().getName() : null)
                .locationId(post.getLocationId())
                .locationName(post.getLocation() != null ? post.getLocation().getAreaName() : null)
                .authorId(post.getAuthor() != null ? post.getAuthor().getId() : null)
                .authorName(post.getAuthor() != null ? post.getAuthor().getFullName() : null)
                .build();
    }

    private Category resolveCategory(CreatePostRequestDto request) {
        if (request.getCategoryId() != null) {
            return categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        String categoryName = normalize(request.getCategoryName());
        if (!StringUtils.hasText(categoryName)) {
            throw new RuntimeException("Category is required");
        }

        return categoryRepository.findFirstByNameIgnoreCase(categoryName)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(categoryName);
                    return categoryRepository.save(category);
                });
    }

    private LocationDemo resolveLocation(Integer locationId) {
        if (locationId == null) {
            throw new RuntimeException("Location is required");
        }
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
    }

    private Post.TransactionType resolveTransactionType(CreatePostRequestDto request) {
        String rawType = firstText(request.getTransactionType(), request.getItemType());
        if (!StringUtils.hasText(rawType)) {
            return Post.TransactionType.FREE;
        }

        String normalized = rawType.trim().toUpperCase(Locale.ROOT)
                .replace("-", "_")
                .replace(" ", "_");

        if ("FOR_SALE".equals(normalized) || "SELL".equals(normalized)) {
            return Post.TransactionType.SALE;
        }
        if ("FREE/BORROW".equals(normalized) || "FREE_BORROW".equals(normalized)
                || "BORROW".equals(normalized) || "GIFT".equals(normalized)) {
            return Post.TransactionType.FREE;
        }

        try {
            return Post.TransactionType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid transaction type");
        }
    }

    private Double resolvePrice(Double price, Post.TransactionType transactionType) {
        if (Post.TransactionType.SALE.equals(transactionType)) {
            if (price == null || price < 0) {
                throw new RuntimeException("Price is required for sale posts");
            }
            return price;
        }
        return price != null ? price : 0D;
    }

    private String resolveDescription(CreatePostRequestDto request) {
        String description = normalize(request.getDescription());
        String condition = normalize(request.getCondition());
        if (!StringUtils.hasText(condition)) {
            return description;
        }
        if (!StringUtils.hasText(description)) {
            return "Condition: " + condition;
        }
        return description + "\n\nCondition: " + condition;
    }

    private String firstText(String first, String second) {
        return StringUtils.hasText(first) ? first : second;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeFilter(String value) {
        String normalized = normalize(value);
        if (!StringUtils.hasText(normalized) || "All".equalsIgnoreCase(normalized)) {
            return null;
        }
        return normalized;
    }
    private String blankToNull(String value) {
        String normalized = normalize(value);
        return StringUtils.hasText(normalized) ? normalized : null;
    }
}