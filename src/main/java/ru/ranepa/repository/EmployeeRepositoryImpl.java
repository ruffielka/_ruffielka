package ru.ranepa.repository;

import ru.ranepa.model.Employee;

import java.util.*;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    private Map<Long, Employee> employees = new HashMap<>();
    private static Long nextId = 1L;

    @Override
    public String save(Employee employee) {
        employee.setId(nextId++);
        employees.put(employee.getId(), employee);
        return "Employee " + employee.getId() + " was saved successfully";
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public Iterable<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    @Override
    public String delete(Long id) {
        if (employees.remove(id) != null) {
            return "Employee " + id + " was deleted successfully";
        }
        return "Employee " + id + " not found";
    }

    // Добавлено для тестирования
    public void clear() {
        employees.clear();
        nextId = 1L;
    }
}