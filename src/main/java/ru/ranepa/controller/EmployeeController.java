package ru.ranepa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ranepa.dto.EmployeeRequestDto;
import ru.ranepa.dto.EmployeeResponseDto;
import ru.ranepa.dto.EmployeeStatsDto;
import ru.ranepa.service.EmployeeService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EmployeeResponseDto> createEmployee(
            @RequestBody @Valid EmployeeRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(requestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<EmployeeResponseDto>> getByPosition(
            @PathVariable String position) {
        return ResponseEntity.ok(employeeService.getByPosition(position));
    }

    @GetMapping("/salary/{salary}")
    public ResponseEntity<List<EmployeeResponseDto>> getBySalaryGreaterThanEqual(
            @PathVariable BigDecimal salary) {
        return ResponseEntity.ok(employeeService.getBySalaryGreaterThanEqual(salary));
    }

    @GetMapping("/stats")
    public ResponseEntity<EmployeeStatsDto> getStatistics() {
        return ResponseEntity.ok(employeeService.getStatistics());
    }
}