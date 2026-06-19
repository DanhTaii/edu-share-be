package vn.edu.nlu.edushare.edu_share.api.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.request.LoginRequest;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.request.RegisterRequest;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.response.LoginResponse;
import vn.edu.nlu.edushare.edu_share.api.auth.dto.response.RegisterResponse;
import vn.edu.nlu.edushare.edu_share.api.auth.service.AuthService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
