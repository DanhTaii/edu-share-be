package vn.edu.nlu.edushare.edu_share.api.transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.nlu.edushare.edu_share.api.article.model.Post;
import vn.edu.nlu.edushare.edu_share.api.article.repository.PostRepository;
import vn.edu.nlu.edushare.edu_share.api.notification.service.NotificationService;
import vn.edu.nlu.edushare.edu_share.api.transaction.dto.request.TransactionRequestDTO;
import vn.edu.nlu.edushare.edu_share.api.transaction.dto.response.TransactionResponseDTO;
import vn.edu.nlu.edushare.edu_share.api.transaction.model.Transaction;
import vn.edu.nlu.edushare.edu_share.api.transaction.repository.TransactionRepository;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;
import vn.edu.nlu.edushare.edu_share.api.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

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

        // TÌM USER MUA HÀNG TỪ DATABASE
        User buyer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người mua!"));

        Transaction transaction = Transaction.builder()
                .post(post)
                .seller(post.getAuthor()) // Gắn Object Seller
                .buyer(buyer)             // Gắn Object Buyer
                .type(Transaction.TransactionType.valueOf(post.getTransactionType().name()))
                .status(Transaction.TransactionStatus.PENDING)
                .build();

        Transaction transaction1 = transactionRepository.save(transaction);

        notificationService.sendTransactionNotification(
                post.getAuthor().getId(),
                "Yêu cầu giao dịch mới \uD83D\uDCE6",
                buyer.getFullName() + " muốn giao dịch sản phẩm: " + post.getTitle(),
                post.getId()
        );

        return transaction1;
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
            dto.setBuyerId(t.getBuyer().getId());
            dto.setSellerId(t.getSeller().getId());
            dto.setTransactionType(String.valueOf(t.getType()));
            dto.setStatus(String.valueOf(t.getStatus()));

            Post post = t.getPost();
            if (post != null) {
                dto.setPostTitle(post.getTitle());
                dto.setPostImage(post.getImageUrl());
                dto.setPrice(post.getPrice());
            }
            User buyer = t.getBuyer();
            if (buyer != null) {
                dto.setBuyerName(buyer.getFullName());
                dto.setBuyerAvatar(buyer.getAvatarUrl());
            }

            User seller = t.getSeller();
            if (seller != null) {
                dto.setSellerName(seller.getFullName());
                dto.setSellerAvatar(seller.getAvatarUrl());
            }
            dtoList.add(dto);
        }
        return dtoList;
    }


    @Transactional
    public void acceptTransaction(Integer transactionId, String currentUserId) {
        Transaction transaction = findTransactionById(transactionId);

        if (!transaction.getSeller().getId().equals(currentUserId)) {
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

        notificationService.sendTransactionNotification(
                transaction.getBuyer().getId(),
                "Giao dịch được chấp nhận \uD83C\uDF89",
                transaction.getSeller().getFullName() + " đã đồng ý giao dịch sản phẩm: " + post.getTitle(),
                post.getId()
        );
    }

    @Transactional
    public void rejectTransaction(Integer transactionId, String currentUserId) {
        Transaction transaction = findTransactionById(transactionId);

        if (!transaction.getSeller().getId().equals(currentUserId)) {
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

        notificationService.sendTransactionNotification(
                transaction.getBuyer().getId(),
                "Giao dịch bị từ chối \uD83D\uDEAB",
                transaction.getSeller().getFullName() + " đã từ chối giao dịch sản phẩm: " + post.getTitle(),
                post.getId()
        );

    }

    @Transactional
    public void completeTransaction(Integer transactionId, String currentUserId) {
        Transaction transaction = findTransactionById(transactionId);

        if (!transaction.getBuyer().getId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền xác nhận hoàn thành giao dịch này!");
        }
        if (!Transaction.TransactionStatus.IN_PROGRESS.equals(transaction.getStatus())) {
            throw new RuntimeException("Chỉ có thể hoàn thành giao dịch đang ở trạng thái ĐANG XỬ LÝ");
        }

        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);

        Post post = transaction.getPost();
        if (post != null) {
            post.setStatus(Post.Status.SOLD);
            postRepository.save(post);
        }

        transactionRepository.save(transaction);

        notificationService.sendTransactionNotification(
                transaction.getSeller().getId(),
                "Giao dịch hoàn tất \uD83E\uDD1D",
                transaction.getBuyer().getFullName() + " đã xác nhận nhận được sản phẩm: " + post.getTitle(),
                post.getId()
        );
    }

    @Transactional
    public void cancelTransaction(Integer transactionId, String currentUserId) {
        Transaction transaction = findTransactionById(transactionId);

        if (!transaction.getBuyer().getId().equals(currentUserId) && !transaction.getSeller().getId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền hủy giao dịch này!");
        }
        if (!Transaction.TransactionStatus.PENDING.equals(transaction.getStatus()) &&
                !Transaction.TransactionStatus.IN_PROGRESS.equals(transaction.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy giao dịch đang ở trạng thái CHỜ XÁC NHẬN hoặc ĐANG XỬ LÝ!");
        }

        transaction.setStatus(Transaction.TransactionStatus.CANCELED);

        Post post = transaction.getPost();
        if (post != null) {
            post.setStatus(Post.Status.AVAILABLE);
            postRepository.save(post);
        }

        transactionRepository.save(transaction);
        // Kiểm tra ai là người hủy để báo cho người còn lại
        if (transaction.getBuyer().getId().equals(currentUserId)) {
            // Nếu người mua hủy -> Báo cho Người Bán
            notificationService.sendTransactionNotification(
                    transaction.getSeller().getId(),
                    "Giao dịch bị hủy \uD83D\uDC94",
                    transaction.getBuyer().getFullName() + " đã hủy giao dịch sản phẩm: " + post.getTitle(),
                    post.getId()
            );
        } else {
            // Nếu Nguười Bán hủy -> Báo cho Người Mua
            notificationService.sendTransactionNotification(
                    transaction.getBuyer().getId(),
                    "Giao dịch bị hủy \uD83D\uDC94",
                    transaction.getSeller().getFullName() + " đã hủy giao dịch sản phẩm: " + post.getTitle(),
                    post.getId()
            );
        }
    }
}