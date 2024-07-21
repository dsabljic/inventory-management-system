package com.inventory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;
    private Long itemId;
    private Long userId;
    private Integer requestedQuantity;
    private LocalDate dateRequested;
    private LocalDate startDate;
    private LocalDate dateApproved;
    private String status;
    private String reason;
}