package vn.edu.nlu.edushare.edu_share.helper;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Android sẽ kết nối vào link này: ws://your-ip:8080/ws-edushare
        registry.addEndpoint("/ws-edushare")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix cho các tin nhắn từ Client gửi lên
        registry.setApplicationDestinationPrefixes("/app");
        // Prefix để Client lắng nghe tin nhắn từ Server
        registry.enableSimpleBroker("/topic", "/queue");
    }
}