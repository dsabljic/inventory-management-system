package com.inventory.backend.service;

import com.inventory.backend.dto.UserDto;
import com.inventory.backend.entity.User;

public interface UserManagementService {
    UserDto register(UserDto registrationRequest);
    UserDto createUser(UserDto newUser);
    UserDto login(UserDto loginRequest);
    UserDto refreshToken(UserDto refreshTokenRequest);
    UserDto getAllUsers();
    UserDto getUsersById(Long id);
    UserDto deleteUser(Long userId);
    UserDto updateUser(Long userId, User updatedUser);
    UserDto getProfileInfo(String email);
    UserDto approveVerification(Long userId);
    void resendVerificationEmail(String email);
}