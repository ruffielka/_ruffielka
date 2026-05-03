package ru.ranepa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ranepa.dto.EmployeeRequestDto;
import ru.ranepa.dto.EmployeeResponseDto;
import ru.ranepa.dto.EmployeeStatsDto;
import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void shouldCalculateAverageSalary() {
        Employee emp1 = Employee.builder()
                .id(1L)
                .name("Ivan")
                .position("Developer")
                .salary(new BigDecimal("100.00"))
                .hireDate(LocalDate.now())
                .build();

        Employee emp2 = Employee.builder()
                .id(2L)
                .name("Petr")
                .position("Manager")
                .salary(new BigDecimal("200.00"))
                .hireDate(LocalDate.now())
                .build();

        Employee emp3 = Employee.builder()
                .id(3L)
                .name("Sergey")
                .position("QA")
                .salary(new BigDecimal("300.00"))
                .hireDate(LocalDate.now())
                .build();

        when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2, emp3));

        EmployeeStatsDto stats = employeeService.getStatistics();

        assertEquals(new BigDecimal("200.00"), stats.getAverageSalary());
        assertEquals(3L, stats.getTotalCount());
    }

    @Test
    void shouldReturnZeroWhenNoEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        EmployeeStatsDto stats = employeeService.getStatistics();

        assertEquals(BigDecimal.ZERO, stats.getAverageSalary());
        assertEquals(0L, stats.getTotalCount());
        assertNull(stats.getTopEarner());
    }

    @Test
    void shouldFindTopEarner() {
        Employee emp1 = Employee.builder()
                .id(1L)
                .name("Ivan")
                .salary(new BigDecimal("100.00"))
                .build();

        Employee emp2 = Employee.builder()
                .id(2L)
                .name("Petr")
                .salary(new BigDecimal("300.00"))
                .build();

        Employee emp3 = Employee.builder()
                .id(3L)
                .name("Sergey")
                .salary(new BigDecimal("200.00"))
                .build();

        when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2, emp3));

        EmployeeStatsDto stats = employeeService.getStatistics();

        assertNotNull(stats.getTopEarner());
        assertEquals("Petr", stats.getTopEarner().getName());
        assertEquals(new BigDecimal("300.00"), stats.getTopEarner().getSalary());
    }

    @Test
    void shouldCreateEmployee() {
        EmployeeRequestDto requestDto = EmployeeRequestDto.builder()
                .name("Test Testov")
                .position("Developer")
                .salary(new BigDecimal("150000.00"))
                .hireDate(LocalDate.of(2024, 1, 15))
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .name(requestDto.getName())
                .position(requestDto.getPosition())
                .salary(requestDto.getSalary())
                .hireDate(requestDto.getHireDate())
                .build();

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDto response = employeeService.createEmployee(requestDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Testov", response.getName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void shouldGetEmployeeById() {
        Employee employee = Employee.builder()
                .id(1L)
                .name("Ivan Ivanov")
                .position("Developer")
                .salary(new BigDecimal("150000.00"))
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponseDto response = employeeService.getEmployeeById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Ivan Ivanov", response.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            employeeService.getEmployeeById(999L);
        });
    }

    @Test
    void shouldDeleteEmployee() {
        Long employeeId = 1L;
        when(employeeRepository.existsById(employeeId)).thenReturn(true);

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository, times(1)).deleteById(employeeId);
    }

    @Test
    void shouldFindByPosition() {
        Employee emp1 = Employee.builder()
                .id(1L)
                .name("Ivan")
                .position("Developer")
                .salary(new BigDecimal("150000.00"))
                .build();

        Employee emp2 = Employee.builder()
                .id(2L)
                .name("Petr")
                .position("Developer")
                .salary(new BigDecimal("200000.00"))
                .build();

        when(employeeRepository.findByPosition("Developer")).thenReturn(List.of(emp1, emp2));

        List<EmployeeResponseDto> result = employeeService.getByPosition("Developer");

        assertEquals(2, result.size());
        assertEquals("Developer", result.get(0).getPosition());
    }
}