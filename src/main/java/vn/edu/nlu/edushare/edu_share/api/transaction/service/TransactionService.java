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
    private final PostRepository postRepository;

    // Hàm helper tìm Transaction theo ID dùng chung
    private Transaction findTransactionById(Integer id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch này!"));
    }

    @Transactional
    public Transaction createTransactionRequest(TransactionRequestDTO request, String currentUserId) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Bài đăng không tồn tại hoặc đã bị xóa!"));

        if (post.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không thể tự yêu cầu giao dịch trên bài đăng của chính mình!");
        }

        boolean isAlreadyPending = transactionRepository.existsByPostIdAndBuyerIdAndStatus(
                post.getId(), currentUserId, Transaction.TransactionStatus.PENDING
        );
        if (isAlreadyPending) {
            throw new RuntimeException("Yêu cầu giao dịch của bạn cho sản phẩm này đang chờ duyệt, không thể gửi thêm!");
        }

        Transaction transaction = Transaction.builder()
                .post(post)
                .sellerId(post.getAuthor().getId())
                .buyerId(currentUserId)
                .type(Transaction.TransactionType.valueOf(post.getTransactionType().name()))
                .status(Transaction.TransactionStatus.PENDING)
                .build();

        return transactionRepository.save(transaction);
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

            Post post = t.getPost();
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
        Transaction transaction = findTransactionById(transactionId);

        if (!transaction.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền duyệt giao dịch này!");
        }
        if (!Transaction.TransactionStatus.PENDING.equals(transaction.getStatus())) {
            throw new RuntimeException("Chỉ có thể duyệt giao dịch đang ở trạng thái CHỜ XÁC NHẬN");
        }

        transaction.setStatus(Transaction.TransactionStatus.IN_PROGRESS);

        // Cập nhật trạng thái bài đăng sang HIDDEN (Ẩn khỏi trang chủ)
        Post post = transaction.getPost();
        if (post != null) {
            post.setStatus(Post.Status.HIDDEN);
            postRepository.save(post);
        }

        transactionRepository.save(transaction);
    }

    @Transactional
    public void rejectTransaction(Integer transactionId, String currentUserId) {
        Transaction transaction = findTransactionById(transactionId);

        if (!transaction.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền từ chối giao dịch này!");
        }
        if (!Transaction.TransactionStatus.PENDING.equals(transaction.getStatus())) {
            throw new RuntimeException("Chỉ có thể từ chối giao dịch đang ở trạng thái CHỜ XÁC NHẬN");
        }

        transaction.setStatus(Transaction.TransactionStatus.REJECTED);

        // Từ chối đơn thì trả trạng thái bài đăng về AVAILABLE (Hiện lại lên chợ)
        Post post = transaction.getPost();
        if (post != null) {
            post.setStatus(Post.Status.AVAILABLE);
            postRepository.save(post);
        }

        transactionRepository.save(transaction);
    }

    @Transactional
    public void completeTransaction(Integer transactionId, String currentUserId) {
        Transaction transaction = findTransactionById(transactionId);

        if (!transaction.getBuyerId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền xác nhận hoàn thành giao dịch này!");
        }
        if (!Transaction.TransactionStatus.IN_PROGRESS.equals(transaction.getStatus())) {
            throw new RuntimeException("Chỉ có thể hoàn thành giao dịch đang ở trạng thái ĐANG XỬ LÝ");
        }

        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);

        // Xác nhận nhận hàng thành công -> Cập nhật bài đăng sang SOLD (Đã bán)
        Post post = transaction.getPost();
        if (post != null) {
            post.setStatus(Post.Status.SOLD);
            postRepository.save(post);
        }

        transactionRepository.save(transaction);
    }

    @Transactional
    public void cancelTransaction(Integer transactionId, String currentUserId) {
        Transaction transaction = findTransactionById(transactionId);

        if (!transaction.getBuyerId().equals(currentUserId) && !transaction.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền hủy giao dịch này!");
        }
        if (!Transaction.TransactionStatus.PENDING.equals(transaction.getStatus()) &&
                !Transaction.TransactionStatus.IN_PROGRESS.equals(transaction.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy giao dịch đang ở trạng thái CHỜ XÁC NHẬN hoặc ĐANG XỬ LÝ!");
        }

        transaction.setStatus(Transaction.TransactionStatus.CANCELED);

        // Hủy đơn giữa chừng -> Trả trạng thái bài đăng về AVAILABLE để người khác mua
        Post post = transaction.getPost();
        if (post != null) {
            post.setStatus(Post.Status.AVAILABLE);
            postRepository.save(post);
        }

        transactionRepository.save(transaction);
    }
}