package com.inventory.backend.repository;

import com.inventory.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long roomId, String status, LocalDate endDate, LocalDate startDate);
    List<Reservation> findByRoomId(Long roomId);
    List<Reservation> findByUserId(Long userId);
}