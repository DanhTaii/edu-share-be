package vn.edu.nlu.edushare.edu_share.api.article.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.service.PostService;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}/summary")
    public ResponseEntity<PostSummaryResponseDto> getPostSummaryById(
            @PathVariable Integer postId
    ) {
        PostSummaryResponseDto postSummary = postService.getPostSummaryById(postId);
        return ResponseEntity.ok(postSummary);
    }

}
