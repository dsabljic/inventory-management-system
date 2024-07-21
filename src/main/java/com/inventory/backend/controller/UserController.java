package com.inventory.backend.controller;

import com.inventory.backend.dto.PasswordResetDto;
import com.inventory.backend.dto.UserDto;
import com.inventory.backend.entity.User;
import com.inventory.backend.service.PasswordResetService;
import com.inventory.backend.service.UserManagementService;
import com.inventory.backend.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto reg) {
        return ResponseEntity.ok(userManagementService.register(reg));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto req) {
        return ResponseEntity.ok(userManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<UserDto> refreshToken(@RequestBody UserDto req) {
        return ResponseEntity.ok(userManagementService.refreshToken(req));
    }

    @PostMapping("admin/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto req) {
        return ResponseEntity.ok(userManagementService.createUser(req));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<UserDto> getAllUsers(){
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/admin/users/{userId}")
    public ResponseEntity<UserDto> getUSerByID(@PathVariable Long userId) {
        return ResponseEntity.ok(userManagementService.getUsersById(userId));
    }

    @PutMapping("/admin/users/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody User reqres) {
        return ResponseEntity.ok(userManagementService.updateUser(userId, reqres));
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<UserDto> deleteUSer(@PathVariable Long userId) {
        return ResponseEntity.ok(userManagementService.deleteUser(userId));
    }

    @GetMapping("/adminUser/profile")
    public ResponseEntity<UserDto> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDto response = userManagementService.getProfileInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/admin/approveVerification/{userId}")
    public ResponseEntity<UserDto> approveVerification(@PathVariable Long userId) {
        return ResponseEntity.ok(userManagementService.approveVerification(userId));
    }

    @PostMapping("/auth/resendVerification")
    public ResponseEntity<Void> resendVerificationEmail(Principal principal) {
        userManagementService.resendVerificationEmail(principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        verificationService.verifyEmail(token);
        return ResponseEntity.ok("Email successfully verified and waiting for admin approval, you can now go back and <a href=\"http://localhost:5173/login\">login</a>.");
    }

    @PostMapping("/auth/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetDto passwordResetDto) {
        passwordResetService.generatePasswordResetToken(passwordResetDto.getEmail());
        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @PostMapping("/auth/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody PasswordResetDto passwordResetDto) {
        passwordResetService.resetPassword(token, passwordResetDto.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully");
    }
}