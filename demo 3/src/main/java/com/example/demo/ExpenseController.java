package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ExpenseController {

    @Autowired
    private ExpenseService service;

    // This is your "Main Menu" rendered as a Web Page
    @PostMapping("/income")
    public ResponseEntity<?> addIncome(@RequestBody IncomeDTO payload

    ) {

        service.addIncome(payload.amount, payload.source, payload.date);
        return ResponseEntity.ok("Income Added Successfully");
    }

    @PostMapping("/expense")
    public ResponseEntity<?> handleExpense(@RequestBody ExpenseDTO payload1) {
        service.addExpense(payload1.amount, payload1.categoryId, payload1.note, payload1.date);
        return ResponseEntity.ok("Expense Added Successfully");
    }

    @GetMapping("/dashboard/summary")
    public Map<String, Object> getDashboardData() {
        return Map.of(
                "totalIncome", service.getTotalIncome(),
                "totalExpense", service.getTotalExpense(),
                "balance", service.getRemainingBalance(),
                "savingsRate", (int) service.getSavingsRate(),
                "healthStatus", service.getHealthStatus(),
                "dailyAllowance", service.getDailyAllowance());
    }

    @GetMapping("/income/total")
    public Map<String, Double> getTotalIncome() {
        return Map.of("totalIncome", service.getTotalIncome());
    }

    @GetMapping("/expense/total")
    public Map<String, Double> getTotalExpense() {
        return Map.of("totalExpense", service.getTotalExpense());
    }

    @GetMapping("/balance")
    public Map<String, Double> getBalance() {
        return Map.of("totalIncome", service.getRemainingBalance());
    }

    @GetMapping("/health")
    public Map<String, Object> gethealth() {
        return Map.of("totalIncome", service.getHealthStatus(),
                "savingsRate", (int) service.getSavingsRate());
    }

    @GetMapping("/transactions")
    public List<Map<String, Object>> getHistory() {
        return service.getFullHistory();
    }

    @GetMapping("/expenses/by-category")
    public Map<String, Double> getCategoryWise() {
        return service.getCategoryData();
    }

    @GetMapping("/categories")
    public List<Map<String, String>> getCategories() {
        return List.of(
                Map.of("id", "cat_01", "name", "Groceries"),
                Map.of("id", "cat_02", "name", "Rent"),
                Map.of("id", "cat_03", "name", "Transport"),
                Map.of("id", "cat_04", "name", "Entertainment"),
                Map.of("id", "cat_05", "name", "Misc")

        );
    }

}