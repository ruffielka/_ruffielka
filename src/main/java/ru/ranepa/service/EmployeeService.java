package ru.ranepa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ranepa.dto.EmployeeRequestDto;
import ru.ranepa.dto.EmployeeResponseDto;
import ru.ranepa.dto.EmployeeStatsDto;
import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public EmployeeResponseDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return toResponseDto(employee);
    }

    public EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto) {
        Employee employee = toEntity(requestDto);
        Employee saved = employeeRepository.save(employee);
        return toResponseDto(saved);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    public List<EmployeeResponseDto> getByPosition(String position) {
        return employeeRepository.findByPosition(position).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public List<EmployeeResponseDto> getBySalaryGreaterThanEqual(BigDecimal salary) {
        return employeeRepository.findBySalaryGreaterThanEqual(salary).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public EmployeeStatsDto getStatistics() {
        List<Employee> employees = employeeRepository.findAll();

        if (employees.isEmpty()) {
            return EmployeeStatsDto.builder()
                    .totalCount(0L)
                    .averageSalary(BigDecimal.ZERO)
                    .topEarner(null)
                    .build();
        }

        BigDecimal avgSalary = employees.stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(employees.size()), 2, RoundingMode.HALF_UP);

        Employee topEarner = employees.stream()
                .max(Comparator.comparing(Employee::getSalary))
                .orElse(null);

        return EmployeeStatsDto.builder()
                .totalCount((long) employees.size())
                .averageSalary(avgSalary)
                .topEarner(topEarner != null ? toResponseDto(topEarner) : null)
                .build();
    }

    private EmployeeResponseDto toResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .hireDate(employee.getHireDate())
                .createdAt(employee.getCreatedAt())
                .build();
    }

    private Employee toEntity(EmployeeRequestDto dto) {
        return Employee.builder()
                .name(dto.getName())
                .position(dto.getPosition())
                .salary(dto.getSalary())
                .hireDate(dto.getHireDate())
                .build();
    }
}