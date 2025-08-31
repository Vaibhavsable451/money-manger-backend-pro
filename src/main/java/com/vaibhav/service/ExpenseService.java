package com.vaibhav.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaibhav.dto.ExpenseDTO;
import com.vaibhav.entity.CategoryEntity;
import com.vaibhav.entity.ExpenseEntity;
import com.vaibhav.entity.ProfileEntity;
import com.vaibhav.repository.CategoryRepository;
import com.vaibhav.repository.ExpenseRepository;
import org.springframework.data.domain.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
 
	private final CategoryRepository categoryRepository;
	private final ExpenseRepository expenseRepository;
	private final ProfileService profileService;
	
	public ExpenseDTO addExpense(ExpenseDTO dto) {
	    ProfileEntity profile = profileService.getCurrentProfile();

	    CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
	        .orElseThrow(() -> new RuntimeException("Category not found")); // ✅ Fix: lambda syntax

	    ExpenseEntity newExpense = toEntity(dto, profile, category); // 🔁 Convert DTO to Entity
	    newExpense = expenseRepository.save(newExpense);             // 💾 Save to DB

	    return toDTO(newExpense); // 🔁 Convert saved Entity to DTO
	}
	
	public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
		ProfileEntity profile = profileService.getCurrentProfile();
		LocalDate now = LocalDate.now();
		LocalDate startDate = now.withDayOfMonth(1);
		LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
		List<ExpenseEntity>list=expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate, endDate);
		return list.stream().map(this::toDTO).toList();
	}
	
	public void deleteExpense(Long expenseId) {
		ProfileEntity profile = profileService.getCurrentProfile();
		ExpenseEntity entity =expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not Found"));
		if(!entity.getProfile().getId().equals(profile.getId())) {
			 throw new RuntimeException("Unauthorized to delete this expense");
		}
		expenseRepository.delete(entity);
		
	}
	
	public List<ExpenseDTO> getLatestExpensesForCurrentUser(){
		ProfileEntity profile = profileService.getCurrentProfile();
		List<ExpenseEntity> list = expenseRepository.findTopByProfileIdOrderByDateDesc(profile.getId());
		return list.stream().map(this::toDTO).toList();
	}
	
	public BigDecimal getTotalExpenseForCurrentUser() {
		try {
			ProfileEntity profile = profileService.getCurrentProfile();
			return expenseRepository.findTotalExpenseByProfileId(profile.getId());
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}
	
	public List<ExpenseDTO> getExpenseForUserOnDate(Long long1, LocalDate date){
		List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(long1,date);
		return list.stream().map(this::toDTO).toList();		
		
	}
	public List<ExpenseDTO> filterExpense(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
		ProfileEntity profile = profileService.getCurrentProfile();
		List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
			profile.getId(), 
			startDate, 
			endDate, 
			keyword, 
			sort
		);
		return list.stream().map(this::toDTO).toList();
	}
	
	private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category){
		return ExpenseEntity.builder()
				.name(dto.getName())
				.icon(dto.getIcon())
				.amount(dto.getAmount())
				.date(dto.getDate())
				.profile(profile)
				.category(category)
				.build();
	}
	
	private ExpenseDTO toDTO(ExpenseEntity entity) {
		return ExpenseDTO.builder()
		.id(entity.getId())
		.name(entity.getName())
		.icon(entity.getIcon())
		.categoryId(entity.getCategory() !=null ? entity.getCategory().getId():null)
		.categoryName(entity.getCategory() !=null ? entity.getCategory().getName():null)
		.amount(entity.getAmount())
		.date(entity.getDate())
		.createdAt(entity.getCreatedAt())
		.updatedAt(entity.getUpdatedAt())
		.build();
	}

}
