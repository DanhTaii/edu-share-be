package vn.edu.nlu.edushare.edu_share.api.article.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.nlu.edushare.edu_share.api.article.dto.request.CreatePostRequestDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostDetailResponseDTO;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostListItemResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostMapResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.service.PostService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostListItemResponseDto>> getPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody CreatePostRequestDto request,
            Authentication authentication
    ) {
        try {
            String currentUserId = (String) authentication.getCredentials();
            PostListItemResponseDto createdPost = postService.createPost(request, currentUserId);
            String location = "/posts/" + createdPost.getId();
            return ResponseEntity
                    .created(URI.create(location))
                    .header(HttpHeaders.LOCATION, location)
                    .body(createdPost);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{postId}/summary")
    public ResponseEntity<PostSummaryResponseDto> getPostSummaryById(
            @PathVariable Integer postId
    ) {
        PostSummaryResponseDto postSummary = postService.getPostSummaryById(postId);
        return ResponseEntity.ok(postSummary);
    }

    @GetMapping("/detail")
    public ResponseEntity<PostDetailResponseDTO> getDetailPost(@RequestParam Integer postId) {
        return ResponseEntity.ok(postService.getDetailPost(postId));
    }
    //
    @GetMapping("/map")
    public ResponseEntity<List<PostMapResponseDto>> getPostsForMap(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(postService.getPostsForMap(area, keyword));
    }

}
