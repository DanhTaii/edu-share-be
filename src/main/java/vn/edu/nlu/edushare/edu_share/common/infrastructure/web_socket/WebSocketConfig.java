package vn.edu.nlu.edushare.edu_share.common.infrastructure.web_socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/*
Khi bạn vừa bấm nút Run để chạy Server, Spring Boot sẽ thực hiện một quá trình gọi là Component Scanning (Quét linh kiện).
Nó đi rà soát toàn bộ các file code trong project của bạn.

Hễ nó nhìn thấy file nào có dán nhãn @Configuration, nó sẽ lập tức tự động mở file đó ra
và chạy các hàm cấu hình bên trong ngay từ lúc khởi động.

Khi nó thấy nhãn @EnableWebSocketMessageBroker,
nó sẽ tự động kích hoạt toàn bộ hệ thống điều phối tin nhắn (Message Broker) ngầm bên trong Server.
*/
@Configuration
// Cấu hình WebSocket cho ứng dụng
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Android sẽ kết nối vào link này: ws://your-ip:8080/ws-edushare
        // 1. Cổng này có SockJS (Dành cho Web)
        registry.addEndpoint("/ws-edushare")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // 2. Cổng này thuần WebSocket (Dành riêng cho Android App kết nối vào)
        //tạo ra một cái cổng chính mang tên /ws-edushare trên Server.
        registry.addEndpoint("/ws-edushare")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix cho các tin nhắn từ Client gửi lên
        registry.setApplicationDestinationPrefixes("/app");
        // Prefix để Client lắng nghe tin nhắn từ Server
        registry.enableSimpleBroker("/topic", "/queue");
    }
}