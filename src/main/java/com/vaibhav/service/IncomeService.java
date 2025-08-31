package com.vaibhav.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vaibhav.dto.ExpenseDTO;
import com.vaibhav.dto.IncomeDTO;
import com.vaibhav.entity.CategoryEntity;
import com.vaibhav.entity.ExpenseEntity;
import com.vaibhav.entity.IncomeEntity;
import com.vaibhav.entity.ProfileEntity;
import com.vaibhav.repository.CategoryRepository;
import com.vaibhav.repository.IncomeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {
	private final CategoryRepository categoryRepository;
	private final IncomeRepository incomeRepository;
	private final ProfileService profileService;
	
	public IncomeDTO addIncome(IncomeDTO dto) {
	    ProfileEntity profile = profileService.getCurrentProfile();

	    CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
	        .orElseThrow(() -> new RuntimeException("Category not found")); // ✅ Fix: lambda syntax

	    IncomeEntity newExpense = toEntity(dto, profile, category); // 🔁 Convert DTO to Entity
	    newExpense = incomeRepository.save(newExpense);             // 💾 Save to DB

	    return toDTO(newExpense); // 🔁 Convert saved Entity to DTO
	}
	
	public List<IncomeDTO> getCurrentMonthExpensesForCurrentUser(){
		ProfileEntity profile = profileService.getCurrentProfile();
		LocalDate now = LocalDate.now();
		LocalDate startDate = now.withDayOfMonth(1);
		LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
		List<IncomeEntity>list=incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate, endDate);
		return list.stream().map(this::toDTO).toList();
	}
	
	
	private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category){
		return IncomeEntity.builder()
				.name(dto.getName())
				.icon(dto.getIcon())
				.amount(dto.getAmount())
				.date(dto.getDate())
				.profile(profile)
				.category(category)
				.build();
	}
	public void deleteExpense(Long expenseId) {
		ProfileEntity profile = profileService.getCurrentProfile();
		IncomeEntity entity =incomeRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Income not Found"));
		if(!entity.getProfile().getId().equals(profile.getId())) {
			 throw new RuntimeException("Unauthorized to delete this Income");
		}
		incomeRepository.delete(entity);
		
	}
	public List<IncomeDTO> getLatestIncomeForCurrentUser(){
		ProfileEntity profile = profileService.getCurrentProfile();
		List<IncomeEntity> list = incomeRepository.findTopByProfileIdOrderByDateDesc(profile.getId());
		return list.stream().map(this::toDTO).toList();
	}
	
	public BigDecimal getTotalIncomeForCurrentUser() {
		ProfileEntity profile = profileService.getCurrentProfile();
		BigDecimal total = incomeRepository.findTotalExpenseByProfileId(profile.getId());
		return total != null ? total : BigDecimal.ZERO;
	}
	public List<IncomeDTO> filterIncome(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
		ProfileEntity profile = profileService.getCurrentProfile();
		List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
			profile.getId(), 
			startDate, 
			endDate, 
			keyword, 
			sort
		);
		return list.stream().map(this::toDTO).toList();
	}
	
	private IncomeDTO toDTO(IncomeEntity entity) {
		return IncomeDTO.builder()
		.id(entity.getId())
		.name(entity.getName())
		.icon(entity.getIcon())
		.categoryId(entity.getCategory() !=null ? entity.getCategory().getId():null)
		.categoryName(entity.getCategory() !=null ? entity.getCategory().getName():"N/A")
		.amount(entity.getAmount())
		.date(entity.getDate())
		.createdAt(entity.getCreatedAt())
		.updatedAt(entity.getUpdatedAt())
		.build();
	}
}
