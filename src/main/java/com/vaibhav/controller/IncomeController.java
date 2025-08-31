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

import com.vaibhav.dto.IncomeDTO;

import com.vaibhav.service.IncomeService;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping("/income")
public class IncomeController {
	private final IncomeService incomeService;
	@PostMapping
	public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO dto){
	IncomeDTO saved = incomeService.addIncome(dto);
	return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	
	}
	
	@GetMapping
	public ResponseEntity<List<IncomeDTO>> getExpense(){
		List<IncomeDTO> expense = incomeService.getCurrentMonthExpensesForCurrentUser();
		return ResponseEntity.ok(expense);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
		incomeService.deleteExpense(id);
		return ResponseEntity.noContent().build();
	}
}
