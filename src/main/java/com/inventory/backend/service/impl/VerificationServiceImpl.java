package com.inventory.backend.service.impl;

import com.inventory.backend.entity.User;
import com.inventory.backend.entity.VerificationToken;
import com.inventory.backend.repository.UserRepository;
import com.inventory.backend.repository.VerificationTokenRepository;
import com.inventory.backend.service.VerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setPendingUser(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }
}