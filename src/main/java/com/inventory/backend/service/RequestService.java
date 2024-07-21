package com.inventory.backend.service;

import com.inventory.backend.dto.RequestDto;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    RequestDto createRequest(RequestDto requestDto);
    List<RequestDto> getRequestsByUserId(Long userId);
    List<RequestDto> getRequestsByItemId(Long itemId);
    List<RequestDto> getAllRequests();
    List<RequestDto> getCancelRequests();
    Optional<RequestDto> getRequestById(Long id);
    RequestDto updateRequestStatus(Long id, String status);
    RequestDto requestReturn(Long id, Long userId);
    RequestDto requestCancellation(Long id, Long userId);
    void deleteRequest(Long id);
    Long getUserIdByEmail(String email);
}