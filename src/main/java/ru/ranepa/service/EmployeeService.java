package ru.ranepa.service;

import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeService {
    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
//п.1 Расчет средней зп
    public BigDecimal calculateAverageSalary() {
        Iterable<Employee> allEmployees = employeeRepository.findAll();
        int count = 0;
        BigDecimal sum = BigDecimal.ZERO;

        for (Employee employee : allEmployees) {
            count++;
            sum = sum.add(employee.getSalary());
        }

        if (count == 0) {
            return BigDecimal.ZERO;
        }

        return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
//п.2 Поиск высокооплачиваемого сотрудника
    public Optional<Employee> findTopEarner() {
        Iterable<Employee> allEmployees = employeeRepository.findAll();
        Employee topEarner = null;

        for (Employee employee : allEmployees) {
            if (topEarner == null || employee.getSalary().compareTo(topEarner.getSalary()) > 0) {
                topEarner = employee;
            }
        }

        return Optional.ofNullable(topEarner);
    }
//п.3 Фильтрация по должности
    public List<Employee> filterByPosition(String position) {
        Iterable<Employee> allEmployees = employeeRepository.findAll();
        List<Employee> result = new ArrayList<>();

        for (Employee employee : allEmployees) {
            if (employee.getPosition().equalsIgnoreCase(position.trim())) {
                result.add(employee);
            }
        }

        return result;
    }
//доп.методы для приложения
    //п.1 создание объекта
    public Employee addEmployee(String name, String position, double salary, LocalDate hireDate) {
        Employee employee = new Employee(salary, position, name, hireDate);
        employeeRepository.save(employee);
        return employee;
    }
    //п.2 получение списка
    public List<Employee> getAllEmployees() {
        List<Employee> result = new ArrayList<>();
        for (Employee employee : employeeRepository.findAll()) {
            result.add(employee);
        }
        return result;
    }
    //п.3 поиск по id
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }
    //п.4 удаление по id
    public boolean deleteEmployee(Long id) {
        String result = employeeRepository.delete(id);
        return result.contains("successfully");
    }
    //п.5 общее количество
    public long getEmployeeCount() {
        long count = 0;
        for (Employee employee : employeeRepository.findAll()) {
            count++;
        }
        return count;
    }
}