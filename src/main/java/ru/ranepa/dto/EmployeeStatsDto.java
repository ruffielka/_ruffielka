package ru.ranepa.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatsDto {

    private Long totalCount;
    private BigDecimal averageSalary;
    private EmployeeResponseDto topEarner;
}