package com.vaibhav.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vaibhav.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    
    @Query("SELECT e FROM ExpenseEntity e JOIN e.category c WHERE c.profile.id = :profileId ORDER BY e.date DESC")
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(@Param("profileId") Long profileId);
    
    @Query("SELECT e FROM ExpenseEntity e JOIN e.category c WHERE c.profile.id = :profileId ORDER BY e.date DESC LIMIT 1")
    List<ExpenseEntity> findTopByProfileIdOrderByDateDesc(@Param("profileId") Long profileId);
    
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseEntity e JOIN e.category c WHERE c.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);
    
    @Query("SELECT e FROM ExpenseEntity e JOIN e.category c WHERE c.profile.id = :profileId AND e.date BETWEEN :startDate AND :endDate AND LOWER(e.name) LIKE LOWER(concat('%', :name, '%'))")
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
        @Param("profileId") Long profileId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        @Param("name") String name, 
        Sort sort
    );
    
    @Query("SELECT e FROM ExpenseEntity e JOIN e.category c WHERE c.profile.id = :profileId AND e.date BETWEEN :startDate AND :endDate")
    List<ExpenseEntity> findByProfileIdAndDateBetween(
        @Param("profileId") Long profileId,
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT e FROM ExpenseEntity e JOIN e.category c WHERE c.profile.id = :profileId AND e.date = :date")
   

	List<ExpenseEntity> findByProfileIdAndDate(@Param("profileId") Long profileId, @Param("date") LocalDate date);
}
