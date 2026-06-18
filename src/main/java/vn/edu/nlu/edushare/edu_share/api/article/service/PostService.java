package vn.edu.nlu.edushare.edu_share.api.article.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.repository.PostRepository;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.ConversationRepository;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.MessageRepository;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostSummaryResponseDto getPostSummaryById(Integer postId) {
        return postRepository.findPostSummaryById(postId);
    }


}
