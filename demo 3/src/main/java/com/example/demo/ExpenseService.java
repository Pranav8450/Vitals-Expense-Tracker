package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {
    @Autowired
    private IncomeRepository incomerepo;
    @Autowired
    private ExpenseRepository expenserepo;

    public void addIncome(double amount, String source, LocalDate date) {
        Income income = new Income();
        income.setAmount(amount);
        income.setSource(source);
        income.setDate(date);
        incomerepo.save(income);
    }

    public void addExpense(double amount, String categoryId, String note, String date) {
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setCategoryId(categoryId);
        expense.setNote(note);
        LocalDate date1 = LocalDate.parse(date);
        expense.setDate(date1);
        expenserepo.save(expense);
    }

    public double getTotalIncome() {
        Double total = incomerepo.sumAllIncome();
        return (total != null) ? total : 0.0;
    }

    public double getTotalExpense() {
        Double total = expenserepo.sumAllExpense();
        return (total != null) ? total : 0.0;
    }

    public double getRemainingBalance() {

        return getTotalIncome() - getTotalExpense();
    }

    public double getDailyAllowance() {
        return 0.0;
    }

    public double getSavingsRate() {
        double income = getTotalIncome();
        if (income <= 0)
            return 0.0;
        double rate = (getRemainingBalance() / getTotalIncome()) * 100;
        return Math.max(0, rate);
    }

    public String getHealthStatus() {
        double rate = getSavingsRate();
        if (rate > 30)
            return "GOOD";
        if (rate > 10)
            return "Average";
        return "POOR";
    }

    // Add these inside your ExpenseService class
    // --------------------------------------------------------------------------
    // SHARED CATEGORY MAP (Acts as the source of truth for names)
    // --------------------------------------------------------------------------
    private static final Map<String, String> CATEGORY_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("cat_01", "Groceries");
        CATEGORY_MAP.put("cat_02", "Rent");
        CATEGORY_MAP.put("cat_03", "Transport");
        CATEGORY_MAP.put("cat_04", "Entertainment");
        CATEGORY_MAP.put("cat_05", "Misc");
    }

    private String resolveCategoryName(String rawId) {
        if (rawId == null)
            return "Other";
        String trimmed = rawId.trim();

        // 1. Direct key match
        if (CATEGORY_MAP.containsKey(trimmed))
            return CATEGORY_MAP.get(trimmed);

        // 2. Direct value match (already a readable name)
        if (CATEGORY_MAP.containsValue(trimmed))
            return trimmed;

        // 3. Normalized key match (e.g. CAT_01)
        for (String key : CATEGORY_MAP.keySet()) {
            if (key.equalsIgnoreCase(trimmed))
                return CATEGORY_MAP.get(key);
        }

        // 4. Strict filtering: If it looks like an ID, mask it as "Other"
        String lower = trimmed.toLowerCase();
        if (lower.startsWith("cat_") || lower.startsWith("inc_") || lower.startsWith("exp_") ||
                lower.matches("^cat\\d+.*") || lower.matches("^inc\\d+.*") || lower.matches("^exp\\d+.*")) {
            return "Other";
        }

        // 5. Final fallback
        return trimmed.length() > 0 ? trimmed : "Other";
    }

    public List<Map<String, Object>> getFullHistory() {
        List<Map<String, Object>> history = new ArrayList<>();

        // 1. Add Incomes with a "type" label
        for (Income i : incomerepo.findAll()) {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("id", "inc_" + i.getId());
            transaction.put("type", "INCOME");
            transaction.put("amount", i.getAmount());
            transaction.put("source", i.getSource());

            // FIX: Set default date if null
            transaction.put("date", (i.getDate() != null) ? i.getDate() : LocalDate.now());

            history.add(transaction);
        }

        // 2. Add Expenses with a "type" label
        for (Expense e : expenserepo.findAll()) {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("id", "exp_" + e.getId());
            transaction.put("type", "EXPENSE");
            transaction.put("amount", e.getAmount());
            transaction.put("categoryId", e.getCategoryId());
            transaction.put("categoryName", resolveCategoryName(e.getCategoryId()));
            transaction.put("note", e.getNote());
            transaction.put("date", (e.getDate() != null) ? e.getDate() : LocalDate.now());
            history.add(transaction);
        }

        return history;
    }

    public Map<String, Double> getCategoryData() {
        List<Object[]> results = expenserepo.getCategorySums();
        Map<String, Double> chartMap = new HashMap<>();

        for (Object[] result : results) {
            String rawId = (result[0] != null) ? (String) result[0] : "Other";
            Double total = (result[1] != null) ? (Double) result[1] : 0.0;
            String displayName = resolveCategoryName(rawId);
            chartMap.put(displayName, chartMap.getOrDefault(displayName, 0.0) + total);
        }
        return chartMap;
    }

}