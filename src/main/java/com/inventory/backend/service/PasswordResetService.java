package com.inventory.backend.service;

public interface PasswordResetService {
    void generatePasswordResetToken(String email);
    void resetPassword(String token, String newPassword);
}