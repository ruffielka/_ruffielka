package ru.ranepa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ranepa.model.Employee;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByPosition(String position);

    List<Employee> findBySalaryGreaterThanEqual(BigDecimal salary);

    List<Employee> findByNameContainingIgnoreCase(String name);
}