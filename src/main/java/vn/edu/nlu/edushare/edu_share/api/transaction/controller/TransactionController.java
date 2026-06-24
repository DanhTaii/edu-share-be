package vn.edu.nlu.edushare.edu_share.api.transaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostDetailResponseDTO;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostListItemResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.PostSummaryResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.service.PostService;
import vn.edu.nlu.edushare.edu_share.api.transaction.dto.request.TransactionRequestDTO;
import vn.edu.nlu.edushare.edu_share.api.transaction.model.Transaction;
import vn.edu.nlu.edushare.edu_share.api.transaction.service.TransactionService;
import vn.edu.nlu.edushare.edu_share.api.user.model.User;
import vn.edu.nlu.edushare.edu_share.api.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping("/request")
    public ResponseEntity<?> requestTransaction(
            @RequestBody TransactionRequestDTO requestDTO,
            Authentication authentication) {


        try {
            String currentUserId = (String) authentication.getCredentials();
            Transaction result = transactionService.createTransactionRequest(requestDTO, currentUserId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptTransaction(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            String currentUserId = (String) authentication.getCredentials();
            // Gọi Service để xử lý duyệt đơn
            transactionService.acceptTransaction(id, currentUserId);
            return ResponseEntity.ok("Duyệt đơn thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectTransaction(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            String currentUserId = (String) authentication.getCredentials();
            // Gọi Service để xử lý từ chối đơn
            transactionService.rejectTransaction(id, currentUserId);
            return ResponseEntity.ok("Từ chối đơn thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
