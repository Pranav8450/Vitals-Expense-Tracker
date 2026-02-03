package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IncomeRepository extends JpaRepository<Income, Integer> {
    @Query("SELECT SUM(i.amount) FROM Income i")
    Double sumAllIncome();

}
