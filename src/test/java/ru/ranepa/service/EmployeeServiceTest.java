package ru.ranepa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepositoryImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private EmployeeRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new EmployeeRepositoryImpl();
        repository.clear();
    }

    @Test
    void shouldCalculateAverageSalary() {
        Employee emp1 = new Employee(100.0, "Developer", "Ivan Ivanov", LocalDate.now());
        Employee emp2 = new Employee(200.0, "Manager", "Petr Petrov", LocalDate.now());
        Employee emp3 = new Employee(300.0, "QA", "Sergey Sergeev", LocalDate.now());

        repository.save(emp1);
        repository.save(emp2);
        repository.save(emp3);

        BigDecimal average = calculateAverageSalary(repository);

        assertEquals(200.0, average.doubleValue(), 0.01);
    }

    @Test
    void shouldReturnZeroWhenNoEmployees() {
        BigDecimal average = calculateAverageSalary(repository);
        assertEquals(BigDecimal.ZERO, average);
    }

    @Test
    void shouldFindTopEarner() {
        Employee emp1 = new Employee(100.0, "Developer", "Ivan Ivanov", LocalDate.now());
        Employee emp2 = new Employee(300.0, "Manager", "Petr Petrov", LocalDate.now());
        Employee emp3 = new Employee(200.0, "QA", "Sergey Sergeev", LocalDate.now());

        repository.save(emp1);
        repository.save(emp2);
        repository.save(emp3);

        Optional<Employee> topEarner = findTopEarner(repository);

        assertTrue(topEarner.isPresent());
        assertEquals("Petr Petrov", topEarner.get().getName());
        assertEquals(300.0, topEarner.get().getSalary().doubleValue());
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoEmployeesForTopEarner() {
        Optional<Employee> topEarner = findTopEarner(repository);
        assertTrue(topEarner.isEmpty());
    }

    @Test
    void shouldFilterEmployeesByPosition() {
        Employee emp1 = new Employee(100.0, "Developer", "Ivan Ivanov", LocalDate.now());
        Employee emp2 = new Employee(200.0, "Manager", "Petr Petrov", LocalDate.now());
        Employee emp3 = new Employee(150.0, "Developer", "Sergey Sergeev", LocalDate.now());

        repository.save(emp1);
        repository.save(emp2);
        repository.save(emp3);

        List<Employee> developers = filterByPosition(repository, "Developer");

        assertEquals(2, developers.size());
        for (Employee emp : developers) {
            assertEquals("Developer", emp.getPosition());
        }
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesWithPosition() {
        Employee emp1 = new Employee(100.0, "Developer", "Ivan Ivanov", LocalDate.now());
        repository.save(emp1);

        List<Employee> managers = filterByPosition(repository, "Manager");

        assertTrue(managers.isEmpty());
    }

    @Test
    void shouldAddEmployeeWithGeneratedId() {
        Employee employee = new Employee(500.0, "Tester", "Test Testov", LocalDate.now());

        String result = repository.save(employee);

        assertNotNull(employee.getId());
        assertEquals(1L, employee.getId());
        assertTrue(result.contains("successfully"));
    }

    @Test
    void shouldFindEmployeeById() {
        Employee employee = new Employee(100.0, "Developer", "Ivan Ivanov", LocalDate.now());
        repository.save(employee);

        Optional<Employee> found = repository.findById(employee.getId());

        assertTrue(found.isPresent());
        assertEquals(employee.getId(), found.get().getId());
        assertEquals(employee.getName(), found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenEmployeeNotFound() {
        Optional<Employee> found = repository.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteEmployee() {
        Employee employee = new Employee(100.0, "Developer", "Ivan Ivanov", LocalDate.now());
        repository.save(employee);

        String result = repository.delete(employee.getId());

        assertTrue(result.contains("successfully"));
        assertTrue(repository.findById(employee.getId()).isEmpty());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentEmployee() {
        String result = repository.delete(999L);
        assertTrue(result.contains("not found"));
    }


    private BigDecimal calculateAverageSalary(EmployeeRepositoryImpl repository) {
        Iterable<Employee> employees = repository.findAll();
        int count = 0;
        BigDecimal sum = BigDecimal.ZERO;

        for (Employee emp : employees) {
            count++;
            sum = sum.add(emp.getSalary());
        }

        if (count == 0) {
            return BigDecimal.ZERO;
        }

        return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    private Optional<Employee> findTopEarner(EmployeeRepositoryImpl repository) {
        Iterable<Employee> employees = repository.findAll();
        Employee topEarner = null;

        for (Employee emp : employees) {
            if (topEarner == null ||
                    emp.getSalary().compareTo(topEarner.getSalary()) > 0) {
                topEarner = emp;
            }
        }

        return Optional.ofNullable(topEarner);
    }

    private List<Employee> filterByPosition(EmployeeRepositoryImpl repository, String position) {
        Iterable<Employee> employees = repository.findAll();
        List<Employee> result = new ArrayList<>();

        for (Employee emp : employees) {
            if (emp.getPosition().equalsIgnoreCase(position.trim())) {
                result.add(emp);
            }
        }

        return result;
    }
}