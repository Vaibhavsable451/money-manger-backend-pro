package com.vaibhav.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhav.dto.ExpenseDTO;
import com.vaibhav.service.ExpenseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {
 
	private final ExpenseService expenseService;
	
	@PostMapping
	public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto){
	ExpenseDTO saved = expenseService.addExpense(dto);
	return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	
	}
	
	@GetMapping
	public ResponseEntity<List<ExpenseDTO>> getExpense(){
		List<ExpenseDTO> expense = expenseService.getCurrentMonthExpensesForCurrentUser();
		return ResponseEntity.ok(expense);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
		expenseService.deleteExpense(id);
		return ResponseEntity.noContent().build();
	}
}
