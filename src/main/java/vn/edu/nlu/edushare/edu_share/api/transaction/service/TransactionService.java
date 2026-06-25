package vn.edu.nlu.edushare.edu_share.api.transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.nlu.edushare.edu_share.api.article.model.Post;
import vn.edu.nlu.edushare.edu_share.api.article.repository.PostRepository;
import vn.edu.nlu.edushare.edu_share.api.transaction.dto.request.TransactionRequestDTO;
import vn.edu.nlu.edushare.edu_share.api.transaction.dto.response.TransactionResponseDTO;
import vn.edu.nlu.edushare.edu_share.api.transaction.model.Transaction;
import vn.edu.nlu.edushare.edu_share.api.transaction.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PostRepository postRepository; // Dùng để tìm kiếm thông tin bài đăng lấy sellerId

    @Transactional
    public Transaction createTransactionRequest(TransactionRequestDTO request, String currentUserId) {

        // 1. Tìm kiếm bài đăng xem có tồn tại không
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Bài đăng không tồn tại hoặc đã bị xóa!"));

        // 2. Bảo mật: Không cho phép tự tạo giao dịch với bài đăng của chính mình
        if (post.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không thể tự yêu cầu giao dịch trên bài đăng của chính mình!");
        }

        // 3. Kiểm tra xem người dùng đã gửi yêu cầu trùng lặp trước đó chưa (Tránh spam click nút)
        boolean isAlreadyPending = transactionRepository.existsByPostIdAndBuyerIdAndStatus(
                post.getId(), currentUserId, Transaction.TransactionStatus.PENDING
        );
        if (isAlreadyPending) {
            throw new RuntimeException("Yêu cầu giao dịch của bạn cho sản phẩm này đang chờ duyệt, không thể gửi thêm!");
        }

        // 4. Khởi tạo đối tượng Transaction và ánh xạ dữ liệu
        Transaction transaction = Transaction.builder()
                .post(post) // Liên kết thực thể Post
                .sellerId(post.getAuthor().getId()) // Lấy ID của chủ bài đăng làm sellerId
                .buyerId(currentUserId)             // Người đăng nhập hiện tại là buyerId
                .type(Transaction.TransactionType.valueOf(post.getTransactionType().name())) // Loại giao dịch ăn theo bài đăng
                .status(Transaction.TransactionStatus.PENDING)  // Mặc định ban đầu luôn là PENDING
                .build();

        // 5. Lưu xuống DB và trả về kết quả
        Transaction savedTransaction = transactionRepository.save(transaction);


        return savedTransaction;
    }

    public List<TransactionResponseDTO> getTransactionHistory(String userId, String role, String status) {
        List<Transaction.TransactionStatus> statusList = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            String[] statusArray = status.split(",");
            for (String s : statusArray) {
                statusList.add(Transaction.TransactionStatus.valueOf(s.trim().toUpperCase()));
            }
        }

        List<Transaction> transactions = transactionRepository.findHistory(userId, role, statusList);

        List<TransactionResponseDTO> dtoList = new ArrayList<>();
        for (Transaction t : transactions) {
            TransactionResponseDTO dto = new TransactionResponseDTO();
            dto.setId(t.getId());
            dto.setPostId(t.getPost().getId());
            dto.setBuyerId(t.getBuyerId());
            dto.setSellerId(t.getSellerId());
            dto.setTransactionType(String.valueOf(t.getType()));
            dto.setStatus(String.valueOf(t.getStatus()));

            Post post = postRepository.findById(t.getPost().getId()).orElse(null);
            if (post != null) {
                dto.setPostTitle(post.getTitle());
                dto.setPostImage(post.getImageUrl());
            }
            dtoList.add(dto);
        }
        return dtoList;
    }
    @Transactional
    public void acceptTransaction(Integer transactionId, String currentUserId) {
        // 1. Tìm giao dịch trong DB
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch này!"));

        // 2. Bảo mật: Chỉ NGƯỜI BÁN (Seller) mới được quyền duyệt/từ chối
        if (!transaction.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền duyệt giao dịch này!");
        }

        // 3. Kiểm tra trạng thái hiện tại (Chỉ duyệt đơn đang CHỜ XÁC NHẬN)
        if (!"PENDING".equalsIgnoreCase(String.valueOf(transaction.getStatus()))) {
            throw new RuntimeException("Chỉ có thể duyệt giao dịch đang ở trạng thái CHỜ XÁC NHẬN");
        }

        // 4. Cập nhật trạng thái thành ĐANG XỬ LÝ (hoặc SUCCESS tùy luồng nghiệp vụ của ông)
        transaction.setStatus(Transaction.TransactionStatus.IN_PROGRESS);

        // 5. Lưu vào DB
        transactionRepository.save(transaction);
    }
    @Transactional
    public void rejectTransaction(Integer transactionId, String currentUserId) {
        // 1. Tìm giao dịch
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch này!"));

        if (!transaction.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền từ chối giao dịch này!");
        }

        // 3. Kiểm tra trạng thái
        if (!"PENDING".equalsIgnoreCase(String.valueOf(transaction.getStatus()))) {
            throw new RuntimeException("Chỉ có thể từ chối giao dịch đang ở trạng thái CHỜ XÁC NHẬN");
        }

        // 4. Cập nhật trạng thái thành TỪ CHỐI
        transaction.setStatus(Transaction.TransactionStatus.REJECTED);

        // 5. Lưu vào DB
        transactionRepository.save(transaction);
    }
    @Transactional
    public void completeTransaction(Integer transactionId, String currentUserId) {
        // 1. Tìm giao dịch
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch này!"));

        // 2. Bảo mật: Chỉ NGƯỜI MUA (Buyer) mới có quyền xác nhận đã nhận hàng!
        if (!transaction.getBuyerId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền xác nhận hoàn thành giao dịch này!");
        }

        // 3. Kiểm tra trạng thái: Chỉ cho phép hoàn thành khi đơn đang ở trạng thái ĐANG XỬ LÝ
        if (!Transaction.TransactionStatus.IN_PROGRESS.equals(transaction.getStatus())) {
            throw new RuntimeException("Chỉ có thể hoàn thành giao dịch đang ở trạng thái ĐANG XỬ LÝ");
        }

        // 4. Cập nhật trạng thái thành THÀNH CÔNG (SUCCESS)
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);

        // 5. Lưu vào DB
        transactionRepository.save(transaction);
    }
    @Transactional
    public void cancelTransaction(Integer transactionId, String currentUserId) {
        // 1. Tìm giao dịch
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch này!"));

        // 2. Bảo mật: Phải là NGƯỜI MUA hoặc NGƯỜI BÁN tham gia vào đơn này mới có quyền hủy
        if (!transaction.getBuyerId().equals(currentUserId) && !transaction.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền hủy giao dịch này!");
        }

        // 3. Kiểm tra trạng thái: Chỉ cho phép hủy khi đơn đang PENDING hoặc IN_PROGRESS
        if (!Transaction.TransactionStatus.PENDING.equals(transaction.getStatus()) &&
                !Transaction.TransactionStatus.IN_PROGRESS.equals(transaction.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy giao dịch đang ở trạng thái CHỜ XÁC NHẬN hoặc ĐANG XỬ LÝ!");
        }

        // 4. Cập nhật trạng thái thành ĐÃ HỦY (CANCELED)
        transaction.setStatus(Transaction.TransactionStatus.CANCELED);

        // 5. Lưu vào DB
        transactionRepository.save(transaction);
    }
}

