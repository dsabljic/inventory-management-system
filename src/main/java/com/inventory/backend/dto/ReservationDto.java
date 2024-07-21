package com.inventory.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReservationDto {
    private Long id;
    private Long userId;
    private Long roomId;
    private int requestedDesks;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDate dateRequested;
    private LocalDate dateApproved;
}