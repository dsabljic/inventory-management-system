package com.inventory.backend.service;

import com.inventory.backend.dto.ReservationDto;
import com.inventory.backend.dto.RoomAvailabilityDto;
import com.inventory.backend.dto.RoomUsageDto;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    ReservationDto createReservation(ReservationDto reservationDto);
    ReservationDto updateReservationStatus(Long id, String status);
    void deleteReservation(Long id);
    ReservationDto getReservationById(Long id);
    List<ReservationDto> getAllReservations();
    List<ReservationDto> getReservationsByUserId(Long userId);
    List<ReservationDto> getReservationsByRoomId(Long roomId);
    List<RoomUsageDto> getReservationHistoryByRoomId(Long roomId);
    ReservationDto requestCancellation(Long id, Long userId);
    ReservationDto requestClearance(Long id, Long userId);
    int getAvailableDesks(Long roomId, LocalDate startDate, LocalDate endDate);
    public List<RoomAvailabilityDto> getAvailableDesksForAllRooms(LocalDate startDate, LocalDate endDate);
    boolean canCreateReservation(Long roomId, LocalDate startDate, LocalDate endDate, int requestedDesks);
    Long getUserIdByEmail(String email);
}
