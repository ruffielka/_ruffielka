package ru.ranepa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ranepa.dto.EmployeeRequestDto;
import ru.ranepa.dto.EmployeeResponseDto;
import ru.ranepa.dto.EmployeeStatsDto;
import ru.ranepa.service.EmployeeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void shouldReturnAllEmployees() throws Exception {
        List<EmployeeResponseDto> employees = List.of(
                EmployeeResponseDto.builder()
                        .id(1L)
                        .name("Ivan Ivanov")
                        .position("Java Developer")
                        .salary(new BigDecimal("150000.00"))
                        .hireDate(LocalDate.of(2024, 1, 15))
                        .build()
        );
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ivan Ivanov"));
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        EmployeeRequestDto request = EmployeeRequestDto.builder()
                .name("Test Testov")
                .position("QA Engineer")
                .salary(new BigDecimal("120000.00"))
                .hireDate(LocalDate.of(2024, 3, 1))
                .build();

        EmployeeResponseDto response = EmployeeResponseDto.builder()
                .id(1L)
                .name("Test Testov")
                .position("QA Engineer")
                .salary(new BigDecimal("120000.00"))
                .hireDate(LocalDate.of(2024, 3, 1))
                .build();

        when(employeeService.createEmployee(request)).thenReturn(response);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Testov"));
    }

    @Test
    void shouldReturnStatistics() throws Exception {
        EmployeeStatsDto stats = EmployeeStatsDto.builder()
                .totalCount(5L)
                .averageSalary(new BigDecimal("135000.00"))
                .build();
        when(employeeService.getStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/employees/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(5));
    }
}