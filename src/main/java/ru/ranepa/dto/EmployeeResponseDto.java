package ru.ranepa.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {

    private Long id;
    private String name;
    private String position;
    private BigDecimal salary;
    private LocalDate hireDate;
    private LocalDateTime createdAt;
}