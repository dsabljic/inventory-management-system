package com.inventory.backend.controller;

import com.inventory.backend.dto.RequestDto;
import com.inventory.backend.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RequestDto> createRequest(@RequestBody RequestDto requestDto, Principal principal) {
        requestDto.setUserId(getUserIdFromPrincipal(principal));
        return ResponseEntity.ok(requestService.createRequest(requestDto));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<RequestDto>> getRequestsByUserId(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(requestService.getRequestsByUserId(userId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RequestDto>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RequestDto> getRequestById(@PathVariable Long id) {
        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/item/{itemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RequestDto>> getRequestsByItemId(@PathVariable Long itemId) {
        return ResponseEntity.ok(requestService.getRequestsByItemId(itemId));
    }

    @GetMapping("/cancelRequests")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RequestDto>> getCancelRequests() {
        return ResponseEntity.ok(requestService.getCancelRequests());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RequestDto> updateRequestStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(requestService.updateRequestStatus(id, status));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RequestDto> requestCancellation(@PathVariable Long id, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(requestService.requestCancellation(id, userId));
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RequestDto> requestReturn(@PathVariable Long id, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(requestService.requestReturn(id, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        String email = principal.getName();
        return requestService.getUserIdByEmail(email);
    }
}