package ru.ranepa;

import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;
import ru.ranepa.repository.EmployeeRepositoryImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.io.*;

public class HrmApplication {

    // Репозиторий для хранения данных
    private static EmployeeRepository repository = new EmployeeRepositoryImpl();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Загружаем сохраненных сотрудников при запуске
        loadFromFile();

        // Проверяем, есть ли сотрудники в репозитории
        boolean hasEmployees = false;
        for (Employee emp : repository.findAll()) {
            hasEmployees = true;
            break;
        }

        // Добавляем тестового сотрудника только если нет данных
        if (!hasEmployees) {
            Employee testEmp = new Employee(
                    40_000.0,
                    "Java developer",
                    "Sokolov Maksim Ivanovich",
                    LocalDate.of(2026, 3, 1)
            );
            repository.save(testEmp);
            System.out.println("Test employee added with ID = 1");
        }

        System.out.println("=== HRM Lite System ===");
        System.out.println("Welcome!");
        System.out.println("The test employee has already been added with the ID = 1\n");

        // п.1 Бесконечный цикл меню
        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showAllEmployees();
                    break;
                case "2":
                    addEmployee();
                    break;
                case "3":
                    deleteEmployee();
                    break;
                case "4":
                    findEmployeeById();
                    break;
                case "5":
                    showStatistics();
                    break;
                case "6":
                    sortEmployees();
                    break;
                case "7":
                    saveToFile();
                    break;
                case "8":
                    loadFromFile();
                    break;
                case "0":
                    saveToFile();
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Wrong choice. Try again.");
            }

            if (!choice.equals("0")) {
                System.out.println("\nClick enter to continue...");
                scanner.nextLine();
            }
        }
    }

    // п.2 Вывод меню
    private static void printMenu() {
        System.out.println("\n=== HRM System Menu ===");
        System.out.println("1. Show all employees");
        System.out.println("2. Add an employee");
        System.out.println("3. Delete an employee");
        System.out.println("4. Find an employee by ID");
        System.out.println("5. Show statistics");
        System.out.println("6. Sort employees");
        System.out.println("7. Save to file");
        System.out.println("8. Load from file");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    // п.3 Показать всех сотрудников
    private static void showAllEmployees() {
        Iterable<Employee> employees = repository.findAll();
        List<Employee> list = new ArrayList<>();
        for (Employee emp : employees) {
            list.add(emp);
        }

        if (list.isEmpty()) {
            System.out.println("Employee list is empty.");
        } else {
            System.out.println("\n=== All Employees ===");
            for (Employee emp : list) {
                System.out.println(emp);
            }
            System.out.println("Total employees: " + list.size());
        }
    }

    // п.4 Добавить сотрудника
    private static void addEmployee() {
        System.out.println("\n=== Add New Employee ===");

        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                System.out.println("Error: name cannot be empty!");
                return;
            }

            System.out.print("Enter position: ");
            String position = scanner.nextLine();
            if (position.trim().isEmpty()) {
                System.out.println("Error: position cannot be empty!");
                return;
            }

            System.out.print("Enter salary: ");
            double salary = Double.parseDouble(scanner.nextLine());
            if (salary <= 0) {
                System.out.println("Error: salary must be positive!");
                return;
            }

            System.out.print("Enter hire date (YYYY-MM-DD): ");
            LocalDate hireDate = LocalDate.parse(scanner.nextLine());

            Employee employee = new Employee(salary, position, name, hireDate);
            String result = repository.save(employee);
            System.out.println(result);

        } catch (NumberFormatException e) {
            System.out.println("Error: salary must be a number!");
        } catch (DateTimeParseException e) {
            System.out.println("Error: invalid date format! Use YYYY-MM-DD");
        }
    }

    // п.5 Удалить сотрудника по ID
    private static void deleteEmployee() {
        System.out.println("\n=== Delete Employee ===");

        try {
            System.out.print("Enter employee ID to delete: ");
            Long id = Long.parseLong(scanner.nextLine());
            String result = repository.delete(id);
            System.out.println(result);
        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be a number!");
        }
    }

    // п.6 Найти сотрудника по ID
    private static void findEmployeeById() {
        System.out.println("\n=== Find Employee by ID ===");

        try {
            System.out.print("Enter employee ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<Employee> employee = repository.findById(id);

            if (employee.isPresent()) {
                Employee emp = employee.get();
                System.out.println("Employee found:");
                System.out.println("  ID: " + emp.getId());
                System.out.println("  Name: " + emp.getName());
                System.out.println("  Position: " + emp.getPosition());
                System.out.println("  Salary: " + emp.getSalary() + " rub.");
                System.out.println("  Hire date: " + emp.getHireDate());
            } else {
                System.out.println("Employee with ID " + id + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be a number!");
        }
    }

    // п.7 Показать статистику
    private static void showStatistics() {
        System.out.println("\n=== Statistics ===");

        // Считаем количество и сумму зарплат
        Iterable<Employee> employees = repository.findAll();
        int count = 0;
        BigDecimal sum = BigDecimal.ZERO;

        Employee topEarner = null;

        for (Employee emp : employees) {
            count++;
            sum = sum.add(emp.getSalary());

            // Поиск самого высокооплачиваемого
            if (topEarner == null ||
                    emp.getSalary().compareTo(topEarner.getSalary()) > 0) {
                topEarner = emp;
            }
        }

        System.out.println("Total employees: " + count);

        if (count > 0) {
            // Средняя зарплата
            BigDecimal average = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            System.out.printf("Average salary: %.2f rub.%n", average);

            // Самый высокооплачиваемый
            System.out.println("Top earner:");
            System.out.println("  Name: " + topEarner.getName());
            System.out.println("  Position: " + topEarner.getPosition());
            System.out.printf("  Salary: %.2f rub.%n", topEarner.getSalary());
        } else {
            System.out.println("No employees in the company.");
        }
    }

    // Доп задание

    // п.8 Сортировка сотрудников
    private static void sortEmployees() {
        System.out.println("\n=== Sort Employees ===");
        System.out.println("1. Sort by name");
        System.out.println("2. Sort by hire date");
        System.out.println("3. Sort by salary");
        System.out.print("Choose sorting option: ");

        String choice = scanner.nextLine();

        List<Employee> employees = new ArrayList<>();
        for (Employee emp : repository.findAll()) {
            employees.add(emp);
        }

        if (employees.isEmpty()) {
            System.out.println("No employees to sort.");
            return;
        }

        switch (choice) {
            case "1":
                employees.sort(Comparator.comparing(Employee::getName));
                System.out.println("\n=== Employees sorted by name ===");
                break;
            case "2":
                employees.sort(Comparator.comparing(Employee::getHireDate));
                System.out.println("\n=== Employees sorted by hire date ===");
                break;
            case "3":
                employees.sort(Comparator.comparing(Employee::getSalary));
                System.out.println("\n=== Employees sorted by salary ===");
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        for (Employee emp : employees) {
            System.out.println(emp);
        }
    }

    // п.9 Сохранение в файл
    private static void saveToFile() {
        System.out.println("\n=== Save to File ===");

        List<Employee> list = new ArrayList<>();
        for (Employee emp : repository.findAll()) {
            list.add(emp);
        }

        if (list.isEmpty()) {
            System.out.println("No employees to save.");
            return;
        }

        try {
            String fileName = "employees.csv";
            FileWriter writer = new FileWriter(fileName);

            writer.write("ID,Name,Position,Salary,HireDate\n");

            for (Employee emp : list) {
                writer.write(emp.getId() + ",");
                writer.write(escapeCsv(emp.getName()) + ",");
                writer.write(escapeCsv(emp.getPosition()) + ",");
                writer.write(emp.getSalary() + ",");
                writer.write(emp.getHireDate() + "\n");
            }

            writer.close();
            System.out.println("Employees saved to: " + fileName);
            System.out.println("Total saved: " + list.size() + " employees");

        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    // п.10 Загрузка из файла
    private static void loadFromFile() {
        String fileName = "employees.csv";
        File file = new File(fileName);

        if (!file.exists()) {
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            int loadedCount = 0;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    try {
                        Long id = Long.parseLong(parts[0]);
                        String name = unescapeCsv(parts[1]);
                        String position = unescapeCsv(parts[2]);
                        double salary = Double.parseDouble(parts[3]);
                        LocalDate hireDate = LocalDate.parse(parts[4]);

                        Employee employee = new Employee(salary, position, name, hireDate);
                        employee.setId(id);
                        repository.save(employee);
                        loadedCount++;
                    } catch (Exception e) {
                        // Пропускаем некорректные строки
                    }
                }
            }

            reader.close();
            if (loadedCount > 0) {
                System.out.println("Loaded " + loadedCount + " employees from file.");
            }

        } catch (IOException e) {
            // Файл не найден - ничего не загружаем
        }
    }

    // Вспомогательный метод для экранирования CSV
    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }

    // Вспомогательный метод для де-экранирования CSV
    private static String unescapeCsv(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
}