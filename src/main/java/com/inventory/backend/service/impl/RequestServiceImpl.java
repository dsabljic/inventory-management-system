package com.inventory.backend.service.impl;

import com.inventory.backend.dto.RequestDto;
import com.inventory.backend.entity.Item;
import com.inventory.backend.entity.Request;
import com.inventory.backend.entity.User;
import com.inventory.backend.repository.ItemRepository;
import com.inventory.backend.repository.RequestRepository;
import com.inventory.backend.repository.UserRepository;
import com.inventory.backend.service.EmailService;
import com.inventory.backend.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public RequestDto createRequest(RequestDto requestDto) {
        Request request = new Request();
        request.setRequestedQuantity(requestDto.getRequestedQuantity());
        request.setDateRequested(LocalDate.now());
        request.setStartDate(requestDto.getStartDate());
        request.setStatus("pending request");
        request.setReason(requestDto.getReason());

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        request.setUser(user);

        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));
        request.setItem(item);

        Request savedRequest = requestRepository.save(request);
        return convertToDto(savedRequest);
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Long userId) {
        return requestRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequestsByItemId(Long itemId) {
        return requestRepository.findByItemId(itemId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getCancelRequests() {
        return requestRepository.findByStatus("pending cancel").stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RequestDto> getRequestById(Long id) {
        return requestRepository.findById(id).map(this::convertToDto);
    }

    @Override
    public RequestDto updateRequestStatus(Long id, String status) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Item item = request.getItem();

        switch (status) {
            case "accepted":
                if (item.getAvailableQuantity() < request.getRequestedQuantity()) {
                    throw new RuntimeException("Requested quantity exceeds available quantity");
                }
                item.setAvailableQuantity(item.getAvailableQuantity() - request.getRequestedQuantity());
                request.setDateApproved(LocalDate.now());
                request.setStatus("active");
                break;

            case "pending return":
                if (!request.getStatus().equals("active")) {
                    throw new RuntimeException("Only active requests can be marked as pending return");
                }
                request.setStatus("pending return");
                break;

            case "completed":
                if (!request.getStatus().equals("pending return")) {
                    throw new RuntimeException("Only pending return requests can be marked as completed");
                }
                item.setAvailableQuantity(item.getAvailableQuantity() + request.getRequestedQuantity());
                request.setStatus("completed");
                break;

            case "canceled":
                if (!request.getStatus().equals("pending cancel")) {
                    throw new RuntimeException("Only pending cancel requests can be marked as canceled");
                }
                request.setStatus("canceled");
                break;

            case "declined":
                if (!request.getStatus().equals("pending request")) {
                    throw new RuntimeException("Only pending requests can be declined");
                }
                request.setStatus("declined");
                break;

            default:
                throw new RuntimeException("Invalid status");
        }

        Request updatedRequest = requestRepository.save(request);
        itemRepository.save(item);
        sendStatusChangeNotification(request);

        return convertToDto(updatedRequest);
    }

    @Override
    public RequestDto requestReturn(Long id, Long userId) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (request.getUser().getId().equals(userId) && request.getStatus().equals("active")) {
            request.setStatus("pending return");
            Request updatedRequest = requestRepository.save(request);
            return convertToDto(updatedRequest);
        } else {
            throw new RuntimeException("Cannot request return for this request");
        }
    }

    @Override
    public RequestDto requestCancellation(Long id, Long userId) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (request.getUser().getId().equals(userId) && request.getStatus().equals("pending request")) {
            request.setStatus("pending cancel");
            Request updatedRequest = requestRepository.save(request);
            return convertToDto(updatedRequest);
        } else {
            throw new RuntimeException("Cannot request cancellation for this request");
        }
    }

    @Override
    public void deleteRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        requestRepository.delete(request);
    }

    private RequestDto convertToDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setUserId(request.getUser().getId());
        requestDto.setItemId(request.getItem().getId());
        requestDto.setRequestedQuantity(request.getRequestedQuantity());
        requestDto.setDateRequested(request.getDateRequested());
        requestDto.setStartDate(request.getStartDate());
        requestDto.setDateApproved(request.getDateApproved());
        requestDto.setStatus(request.getStatus());
        requestDto.setReason(request.getReason());
        return requestDto;
    }

    @Override
    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    private void sendStatusChangeNotification(Request request) {
        String subject = "Status Update for Your Request";
        String message = String.format("Hello %s,\n\nThe status of your request for item '%s' (quantity: %d) has been updated to '%s'.\n\nBest regards,\nInventory Team",
                request.getUser().getName(), request.getItem().getName(), request.getRequestedQuantity(), request.getStatus());
        emailService.send(request.getUser().getEmail(), subject, message);
    }
}