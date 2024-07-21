package com.inventory.backend.controller;

import com.inventory.backend.dto.ReservationDto;
import com.inventory.backend.dto.RoomAvailabilityDto;
import com.inventory.backend.dto.RoomUsageDto;
import com.inventory.backend.service.ReservationService;
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
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationDto reservationDto, Principal principal) {
        reservationDto.setUserId(getUserIdFromPrincipal(principal));
        return ResponseEntity.ok(reservationService.createReservation(reservationDto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<ReservationDto>> getUserReservations(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
    }

    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReservationDto>> getRoomReservations(@PathVariable Long roomId) {
        return ResponseEntity.ok(reservationService.getReservationsByRoomId(roomId));
    }

    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<RoomUsageDto>> getReservationHistoryByRoomId(@PathVariable Long roomId) {
        List<RoomUsageDto> history = reservationService.getReservationHistoryByRoomId(roomId);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ReservationDto> updateReservationStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }

    @PutMapping("/{id}/requestCancel")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReservationDto> requestCancellation(@PathVariable Long id, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(reservationService.requestCancellation(id, userId));
    }

    @PutMapping("/{id}/requestClearance")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReservationDto> requestClearance(@PathVariable Long id, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(reservationService.requestClearance(id, userId));
    }

    @GetMapping("/availability")
    public ResponseEntity<Integer> getAvailableDesks(@RequestParam Long roomId, @RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(reservationService.getAvailableDesks(roomId, start, end));
    }

    @GetMapping("/rooms/availability")
    public ResponseEntity<List<RoomAvailabilityDto>> getAvailableDesksForAllRooms(@RequestParam("startDate") LocalDate startDate,
                                                                                  @RequestParam("endDate") LocalDate endDate) {
        List<RoomAvailabilityDto> availability = reservationService.getAvailableDesksForAllRooms(startDate, endDate);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/checkAvailability")
    public ResponseEntity<Boolean> canCreateReservation(@RequestParam Long roomId, @RequestParam String startDate,
                                                        @RequestParam String endDate, @RequestParam int requestedDesks) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        boolean canCreate = reservationService.canCreateReservation(roomId, start, end, requestedDesks);
        return ResponseEntity.ok(canCreate);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        String email = principal.getName();
        return reservationService.getUserIdByEmail(email);
    }
}