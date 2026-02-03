package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e")
    Double sumAllExpense();

    @Query("SELECT e.categoryId, SUM(e.amount) FROM Expense e GROUP BY e.categoryId")
    List<Object[]> getCategorySums();

}
