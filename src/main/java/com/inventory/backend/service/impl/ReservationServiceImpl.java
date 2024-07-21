package com.inventory.backend.service.impl;

import com.inventory.backend.dto.ReservationDto;
import com.inventory.backend.dto.RoomAvailabilityDto;
import com.inventory.backend.dto.RoomUsageDto;
import com.inventory.backend.entity.Reservation;
import com.inventory.backend.entity.Room;
import com.inventory.backend.entity.User;
import com.inventory.backend.repository.ReservationRepository;
import com.inventory.backend.repository.RoomRepository;
import com.inventory.backend.repository.UserRepository;
import com.inventory.backend.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public ReservationDto createReservation(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        reservation.setRequestedDesks(reservationDto.getRequestedDesks());
        reservation.setStartDate(reservationDto.getStartDate());

        if (reservationDto.getEndDate() == null) {
            reservation.setEndDate(reservationDto.getStartDate());
        } else {
            reservation.setEndDate(reservationDto.getEndDate());
        }

        reservation.setStatus("pending request");
        reservation.setDateRequested(LocalDate.now());

        Room room = roomRepository.findById(reservationDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        reservation.setRoom(room);

        User user = userRepository.findById(reservationDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        reservation.setUser(user);

        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToDto(savedReservation);
    }

    @Override
    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return convertToDto(reservation);
    }

    @Override
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getReservationsByRoomId(Long roomId) {
        return reservationRepository.findByRoomId(roomId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomUsageDto> getReservationHistoryByRoomId(Long roomId) {
        List<Reservation> reservations = reservationRepository.findByRoomId(roomId).stream()
                .filter(reservation -> reservation.getStatus().equals("completed") || reservation.getStatus().equals("pending clearance") || reservation.getStatus().equals("reserved"))
                .collect(Collectors.toList());

        TreeMap<LocalDate, List<String>> usageMap = new TreeMap<>();
        for (Reservation reservation : reservations) {
            LocalDate date = reservation.getStartDate();
            usageMap.computeIfAbsent(date, k -> new ArrayList<>()).add(reservation.getUser().getName());
        }

        return usageMap.entrySet().stream()
                .map(entry -> new RoomUsageDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDto updateReservationStatus(Long id, String status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        switch (status) {
            case "reserved":
                if (!reservation.getStatus().equals("pending request")) {
                    throw new RuntimeException("Only pending requests can be reserved");
                }
                if (!canCreateReservation(reservation.getRoom().getId(), reservation.getStartDate(), reservation.getEndDate(), reservation.getRequestedDesks())) {
                    throw new RuntimeException("Not enough available desks for this reservation");
                }
                reservation.setStatus("reserved");
                reservation.setDateApproved(LocalDate.now());
                break;

            case "pending clearance":
                if (!reservation.getStatus().equals("reserved")) {
                    throw new RuntimeException("Only reserved requests can be marked as pending clearance");
                }
                reservation.setStatus("pending clearance");
                break;

            case "completed":
                if (!reservation.getStatus().equals("pending clearance")) {
                    throw new RuntimeException("Only pending clearance requests can be marked as completed");
                }
                reservation.setStatus("completed");
                break;

            case "canceled":
                if (!reservation.getStatus().equals("pending cancel")) {
                    throw new RuntimeException("Only pending cancel requests can be marked as canceled");
                }
                reservation.setStatus("canceled");
                break;

            case "declined":
                if (!reservation.getStatus().equals("pending request")) {
                    throw new RuntimeException("Only pending requests can be declined");
                }
                reservation.setStatus("declined");
                break;

            default:
                throw new RuntimeException("Invalid status");
        }

        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToDto(updatedReservation);
    }

    @Override
    public ReservationDto requestCancellation(Long id, Long userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        if (reservation.getUser().getId().equals(userId) && reservation.getStatus().equals("pending request")) {
            reservation.setStatus("pending cancel");
            Reservation updatedReservation = reservationRepository.save(reservation);
            return convertToDto(updatedReservation);
        } else {
            throw new RuntimeException("Cannot request cancellation for this reservation");
        }
    }

    @Override
    public ReservationDto requestClearance(Long id, Long userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        if (reservation.getUser().getId().equals(userId) && reservation.getStatus().equals("reserved")) {
            reservation.setStatus("pending clearance");
            Reservation updatedReservation = reservationRepository.save(reservation);
            return convertToDto(updatedReservation);
        } else {
            throw new RuntimeException("Cannot request clearance for this reservation");
        }
    }

    @Override
    public int getAvailableDesks(Long roomId, LocalDate startDate, LocalDate endDate) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        List<Reservation> reservations = reservationRepository.findByRoomId(roomId);
        int totalDesks = room.getTotalDesks();
        int reservedDesks = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDate currentDate = date;
            int dailyReservedDesks = (int) reservations.stream()
                    .filter(reservation -> reservation.getStatus().equals("reserved") || reservation.getStatus().equals("pending clearance"))
                    .filter(reservation -> !reservation.getEndDate().isBefore(currentDate) && !reservation.getStartDate().isAfter(currentDate))
                    .mapToInt(Reservation::getRequestedDesks)
                    .sum();
            reservedDesks = Math.max(reservedDesks, dailyReservedDesks);
        }

        return totalDesks - reservedDesks;
    }

    @Override
    public List<RoomAvailabilityDto> getAvailableDesksForAllRooms(LocalDate startDate, LocalDate endDate) {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(room -> new RoomAvailabilityDto(room.getId(), getAvailableDesks(room.getId(), startDate, endDate)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canCreateReservation(Long roomId, LocalDate startDate, LocalDate endDate, int requestedDesks) {
        return getAvailableDesks(roomId, startDate, endDate) >= requestedDesks;
    }

    @Override
    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @Override
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        if (reservation.getStatus().equals("reserved") || reservation.getStatus().equals("pending clearance")) {
            throw new RuntimeException("Cannot delete reservation with status reserved or pending clearance");
        }
        reservationRepository.delete(reservation);
    }

    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setUserId(reservation.getUser().getId());
        reservationDto.setRoomId(reservation.getRoom().getId());
        reservationDto.setRequestedDesks(reservation.getRequestedDesks());
        reservationDto.setDateRequested(reservation.getDateRequested());
        reservationDto.setDateApproved(reservation.getDateApproved());
        reservationDto.setStartDate(reservation.getStartDate());
        reservationDto.setEndDate(reservation.getEndDate());
        reservationDto.setStatus(reservation.getStatus());
        return reservationDto;
    }
}