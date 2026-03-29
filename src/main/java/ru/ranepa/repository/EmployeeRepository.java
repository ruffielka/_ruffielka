package ru.ranepa.repository;

import ru.ranepa.model.Employee;

import java.util.Optional;

public interface EmployeeRepository {
    String save(Employee employee);
    Optional<Employee> findById(Long Id);
    Iterable<Employee> findAll();
    String delete(Long id);
}
