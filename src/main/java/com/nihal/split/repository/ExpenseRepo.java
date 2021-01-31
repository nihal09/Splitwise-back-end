package com.nihal.split.repository;

import com.nihal.split.models.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpenseRepo extends MongoRepository<Expense,String> {
}
