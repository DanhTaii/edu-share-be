package vn.edu.nlu.edushare.edu_share.api.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.nlu.edushare.edu_share.api.chat.dto.ChatMessageDto;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Conversation;
import vn.edu.nlu.edushare.edu_share.api.chat.model.Message;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.ConversationRepository;
import vn.edu.nlu.edushare.edu_share.api.chat.repository.MessageRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public Message processAndSaveMessage(ChatMessageDto chatMessageDto) {
        Conversation conversation = conversationRepository.findByPostIdAndUserOneIdAndUserTwoId(
                chatMessageDto.getPostId(), chatMessageDto.getSenderId(), chatMessageDto.getRecipientId()
        ).orElseGet(() -> {
            Conversation newConv = new Conversation();
            newConv.setPostId(chatMessageDto.getPostId());
            newConv.setUserOneId(chatMessageDto.getSenderId());
            newConv.setUserTwoId(chatMessageDto.getRecipientId());
            return conversationRepository.save(newConv);
        });

        conversation.setLastMessage(chatMessageDto.getContent());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(chatMessageDto.getSenderId());
        message.setContent(chatMessageDto.getContent());
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());

        return messageRepository.save(message);

    }
}
