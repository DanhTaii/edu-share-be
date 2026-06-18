### **Tài liệu Kỹ thuật: Chức năng Chat Real-time trong Edu-Share**

Dưới đây là phân tích đầy đủ về luồng hoạt động, các thành phần chính và công nghệ được sử dụng.

---

#### **1. Tổng quan về Công nghệ và Kiến trúc**

Chức năng chat được xây dựng theo kiến trúc "lai" (hybrid), kết hợp giữa **REST API** và **WebSocket** để đạt được hiệu quả tối ưu:

*   **REST API (Sử dụng Spring Web MVC):** Dùng để xử lý các tác vụ không yêu cầu tính tức thời như:
    *   Tải lịch sử các cuộc trò chuyện.
    *   Tải lịch sử tin nhắn trong một cuộc trò chuyện cụ thể.
    *   Gửi một tin nhắn mới (hành động khởi tạo).
*   **WebSocket (Sử dụng Spring WebSocket với STOMP):** Dùng để đẩy (push) tin nhắn mới đến người nhận trong thời gian thực, ngay sau khi tin nhắn được lưu vào cơ sở dữ liệu.

**Các công nghệ cốt lõi:**

*   **Spring Boot:** Nền tảng chính của ứng dụng.
*   **Spring WebSocket:** Cung cấp hỗ trợ để tạo các ứng dụng web tương tác hai chiều.
*   **STOMP (Simple Text Oriented Messaging Protocol):** Một giao thức con hoạt động trên WebSocket, giúp đơn giản hóa việc giao tiếp bằng cách định nghĩa các định dạng tin nhắn như "SEND", "SUBSCRIBE". Spring sử dụng nó để tạo mô hình lập trình `topic-based` (dựa trên chủ đề) quen thuộc.
*   **Spring Data JPA:** Để tương tác với cơ sở dữ liệu (lưu và truy vấn tin nhắn/cuộc trò chuyện).
*   **`SimpMessagingTemplate`:** Một class của Spring giúp gửi tin nhắn đến các "broker channel" (kênh môi giới), từ đó đẩy đến các client đã đăng ký (subscribed).

---

#### **2. Luồng hoạt động chi tiết của một tin nhắn**

Đây là vòng đời của một tin nhắn từ khi được gửi đến khi người nhận nhận được:

1.  **Phía Client (Giao diện người dùng):**
    *   Khi người dùng đăng nhập và mở giao diện chat, client sẽ thiết lập một kết nối WebSocket đến server tại một endpoint được cấu hình (ví dụ: `/ws`).
    *   Sau khi kết nối thành công, client sẽ **đăng ký (subscribe)** vào một `topic` (chủ đề) duy nhất dành riêng cho mình. Dựa trên mã nguồn, `topic` này có định dạng là `/topic/messages/{userId}`. Điều này đảm bảo rằng chỉ người dùng đó mới nhận được tin nhắn gửi đến cho họ.
    *   Khi người dùng gõ và gửi một tin nhắn, client sẽ thực hiện một lời gọi **`POST` đến REST API** tại endpoint: `POST /conversations/{conversationId}/messages`.

2.  **Phía Server - `MessageRestController`:**
    *   Endpoint `POST /conversations/{conversationId}/messages` trong `MessageRestController.java` tiếp nhận yêu cầu.
    *   Nó nhận vào `conversationId` từ URL và `MessageRequestDto` (chứa nội dung tin nhắn, người gửi) từ body của request.
    *   Controller gọi phương thức `chatService.saveMessage(chatMessageDto)`.

3.  **Phía Server - `ChatService` (Nơi xử lý logic chính):**
    *   Phương thức `saveMessage` trong `ChatService.java` là trung tâm của toàn bộ quá trình:
        a.  **Xác thực và Tìm kiếm:** Tìm `Conversation` (cuộc trò chuyện) trong cơ sở dữ liệu dựa trên `conversationId`. Nó cũng xác thực để đảm bảo người gửi (`senderId`) thực sự là một thành viên của cuộc trò chuyện đó.
        b.  **Lưu vào Cơ sở dữ liệu:** Tạo một đối tượng `Message`, thiết lập nội dung, người gửi, và lưu nó vào cơ sở dữ liệu thông qua `messageRepository.save()`. Đây là bước đảm bảo tin nhắn được lưu trữ vĩnh viễn.
        c.  **Cập nhật Cuộc trò chuyện:** Cập nhật trường `lastMessage` và `updatedAt` của `Conversation` để hiển thị tin nhắn cuối cùng ra danh sách các cuộc trò chuyện.
        d.  **Đẩy tin nhắn Real-time (Bước quan trọng nhất):**
            *   Sử dụng đối tượng `SimpMessagingTemplate` đã được inject vào.
            *   Xác định `destination` (đích đến) của người nhận. Ví dụ: `/topic/messages/{recipientId}`.
            *   Gọi `messagingTemplate.convertAndSend(destination, messageResponseDto)`. Lệnh này sẽ gửi tin nhắn đến "message broker" của Spring.
            *   Broker sau đó sẽ tìm tất cả các client đang `subscribe` vào `topic` này và đẩy tin nhắn đến họ qua kết nối WebSocket đang mở.
        e.  **Đồng bộ hóa cho người gửi:** Dịch vụ cũng gửi một bản sao của tin nhắn đến `topic` của chính người gửi (`/topic/messages/{senderId}`). Điều này rất hữu ích để đồng bộ hóa cuộc trò chuyện nếu người gửi đang mở ứng dụng trên nhiều thiết bị (ví dụ: web và điện thoại).

4.  **Phía Client (Giao diện người nhận):**
    *   Client của người nhận, vốn đã `subscribe` vào `topic` `/topic/messages/{recipientId}`, sẽ ngay lập tức nhận được `messageResponseDto` qua kết nối WebSocket.
    *   Giao diện người dùng sau đó chỉ cần hiển thị tin nhắn mới này lên màn hình chat mà không cần phải tải lại trang.

---

#### **3. Phân tích các thành phần mã nguồn chính**

*   **`MessageRestController.java`:**
    *   **Vai trò:** Cổng vào (entry-point) cho các hành động liên quan đến chat thông qua REST.
    *   **Kỹ thuật:** Sử dụng các annotation `@RestController`, `@GetMapping`, `@PostMapping` tiêu chuẩn của Spring MVC. Phân trang kết quả bằng `Pageable`.

*   **`ChatService.java`:**
    *   **Vai trò:** Bộ não của chức năng chat. Chịu trách nhiệm điều phối giữa việc lưu trữ dữ liệu và gửi tin nhắn real-time.
    *   **Kỹ thuật:**
        *   `@Service`: Đánh dấu đây là một lớp dịch vụ.
        *   `@RequiredArgsConstructor`: Tự động inject các dependency (`MessageRepository`, `SimpMessagingTemplate`, v.v.) qua constructor, một thực hành tốt cho DI.
        *   `SimpMessagingTemplate`: Công cụ chính để gửi tin nhắn qua WebSocket. Nó trừu tượng hóa sự phức tạp của việc quản lý kết nối và gửi dữ liệu.
        *   `@Transactional`: Đảm bảo rằng các thao tác trong `saveMessage` (lưu tin nhắn, cập nhật cuộc trò chuyện) diễn ra như một giao dịch duy nhất. Nếu có lỗi, tất cả sẽ được rollback.

*   **`WebSocketConfig.java` (Giả định, vì chưa thấy file nhưng chắc chắn phải có):**
    *   **Vai trò:** Cấu hình cơ sở hạ tầng WebSocket cho ứng dụng.
    *   **Kỹ thuật (dự kiến):**
        *   `@Configuration` và `@EnableWebSocketMessageBroker`: Kích hoạt tính năng message broker trên WebSocket.
        *   `registerStompEndpoints()`: Đăng ký một endpoint cho client kết nối ban đầu (ví dụ: `/ws`). Đây là endpoint cho "bắt tay" WebSocket.
        *   `configureMessageBroker()`: Cấu hình các tiền tố cho `topic` (ví dụ: `/topic`) mà client sẽ subscribe, và các tiền tố cho `application` (ví dụ: `/app`) mà client sẽ gửi tin nhắn đến (nếu có).

*   **`MessageRepository` & `ConversationRepository`:**
    *   **Vai trò:** Giao tiếp với cơ sở dữ liệu.
    *   **Kỹ thuật:** Sử dụng Spring Data JPA. Chỉ cần định nghĩa interface và các phương thức truy vấn tùy chỉnh (ví dụ: `findUserConversations`), Spring sẽ tự động tạo ra các implementation tương ứng.

---

#### **4. Tổng kết các kỹ thuật nổi bật**

1.  **Kiến trúc Hybrid (REST + WebSocket):** Tận dụng điểm mạnh của cả hai. REST cho độ tin cậy và các thao tác trạng thái, WebSocket cho tốc độ và tính tức thời.
2.  **Topic-based Messaging:** Mỗi người dùng `subscribe` vào một kênh riêng. Điều này giúp việc gửi tin nhắn đến đúng người nhận trở nên đơn giản, an toàn và hiệu quả, thay vì phát sóng (broadcast) cho tất cả mọi người.
3.  **Dependency Injection:** Sử dụng `@RequiredArgsConstructor` của Lombok để giữ mã nguồn sạch sẽ và tuân thủ các nguyên tắc SOLID.
4.  **Tách biệt Trách nhiệm (Separation of Concerns):** Mã nguồn được tổ chức tốt thành các lớp Controller, Service, và Repository, mỗi lớp có một nhiệm vụ rõ ràng.
5.  **Sử dụng DTO (Data Transfer Object):** Các lớp như `MessageRequestDto` và `MessageResponseDto` được dùng để định hình dữ liệu trao đổi qua API, giúp che giấu cấu trúc của các entity cơ sở dữ liệu và chỉ exposé những thông tin cần thiết.
