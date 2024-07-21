package com.inventory.backend.service.impl;

import com.inventory.backend.dto.UserDto;
import com.inventory.backend.entity.User;
import com.inventory.backend.entity.VerificationToken;
import com.inventory.backend.repository.UserRepository;
import com.inventory.backend.repository.VerificationTokenRepository;
import com.inventory.backend.service.EmailService;
import com.inventory.backend.service.JWTUtils;
import com.inventory.backend.service.UserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public UserDto register(UserDto registrationRequest) {
        log.info("Registration request: {}", registrationRequest);
        UserDto response = saveUser(registrationRequest, true);
//        sendVerificationEmail(response.getUser());
        return response;
    }

    @Override
    public UserDto createUser(UserDto newUser) {
        log.info("User creation request: {}", newUser);
        return saveUser(newUser, false);
    }

    @Override
    public UserDto login(UserDto loginRequest) {
        UserDto response = new UserDto();
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            String jwt = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setVerified(user.getVerified());
            response.setPendingUser(user.getPendingUser());

            if (!user.getPendingUser() && !user.getVerified()) {
                sendVerificationEmail(user);
                response.setMessage("Please verify your email first. Verification email has been sent.");
            } else if (user.getPendingUser() && !user.getVerified()) {
                response.setMessage("Your account is pending admin approval.");
            } else if (user.getVerified()) {
                response.setMessage("Successfully signed in");
            }

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public UserDto refreshToken(UserDto refreshTokenRequest) {
        UserDto response = new UserDto();
        try {
            String email = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)) {
                String jwt = jwtUtils.generateToken(user);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully refreshed token");
            } else {
                response.setStatusCode(401);
                response.setMessage("Invalid token");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public UserDto getAllUsers() {
        UserDto response = new UserDto();
        try {
            List<User> users = userRepository.findAll();
            if (!users.isEmpty()) {
                response.setUsers(users);
                response.setStatusCode(200);
                response.setMessage("Successful");
            } else {
                response.setStatusCode(404);
                response.setMessage("No users found");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred: " + e.getMessage());
        }
        return response;
    }

    @Override
    public UserDto getUsersById(Long id) {
        UserDto response = new UserDto();
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            response.setUser(user);
            response.setStatusCode(200);
            response.setMessage("User found successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred: " + e.getMessage());
        }
        return response;
    }

    @Override
    public UserDto deleteUser(Long userId) {
        UserDto response = new UserDto();
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                userRepository.deleteById(userId);
                response.setStatusCode(200);
                response.setMessage("User deleted successfully");
            } else {
                response.setStatusCode(404);
                response.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public UserDto updateUser(Long userId, User updatedUser) {
        UserDto response = new UserDto();
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setEmail(updatedUser.getEmail());
            user.setName(updatedUser.getName());
            user.setRole(updatedUser.getRole());
//            user.setVerified(updatedUser.getVerified());
//            user.setPendingUser(updatedUser.getPendingUser());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            User savedUser = userRepository.save(user);
            response.setUser(savedUser);
            response.setStatusCode(200);
            response.setMessage("User updated successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return response;
    }

    @Override
    public UserDto getProfileInfo(String email) {
        UserDto response = new UserDto();
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            response.setUser(user);
            response.setStatusCode(200);
            response.setMessage("successful");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return response;
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        sendVerificationEmail(user);
    }

    @Override
    public UserDto approveVerification(Long userId) {
        UserDto response = new UserDto();
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getPendingUser()) {
                response.setStatusCode(400);
                response.setMessage("User email is not verified.");
                return response;
            }

            user.setPendingUser(false);
            user.setVerified(true);
            user.setRole("USER");

            User savedUser = userRepository.save(user);
            response.setUser(savedUser);
            response.setStatusCode(200);
            response.setMessage("User verification approved successfully");

            String subject = "Account Approval";
            String message = "Your email has been verified and approved by the admin. You can now <a href=\"http://localhost:5173/login\">login</a>.";

            emailService.send(user.getEmail(), subject, message);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while approving user verification: " + e.getMessage());
        }
        return response;
    }

    private UserDto saveUser(UserDto userDto, boolean registration) {
        UserDto response = new UserDto();
        try {
            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setName(userDto.getName());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            if (registration) {
                user.setRole("PENDING_USER");
                user.setVerified(false);
                user.setPendingUser(false);
            } else {
                if (userDto.getRole().equals("ADMIN")) {
                    user.setRole("ADMIN");
                    user.setVerified(true);
                    user.setPendingUser(false);
                } else if (userDto.getRole().equals("USER")) {
                    user.setRole("PENDING_USER");
                    user.setVerified(false);
                    user.setPendingUser(false);
                }
            }
            User savedUser = userRepository.save(user);
            response.setUser(savedUser);
            response.setMessage("User saved successfully");
            response.setStatusCode(200);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    private void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        verificationTokenRepository.save(verificationToken);

        String verificationUrl = "https://localhost:8443/auth/verify?token=" + token;
        String subject = "Email Verification";
        String message = "Please click the following link to verify your email: " + "<a href=\"" + verificationUrl + "\">Activate Now</a>";

        emailService.send(user.getEmail(), subject, message);
    }
}