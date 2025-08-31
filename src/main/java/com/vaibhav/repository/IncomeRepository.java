package com.vaibhav.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vaibhav.entity.IncomeEntity;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    @Query("SELECT i FROM IncomeEntity i WHERE i.category.profile.id = :profileId ORDER BY i.date DESC")
    List<IncomeEntity> findByProfileIdOrderByDateDesc(@Param("profileId") Long profileId);

    @Query("SELECT i FROM IncomeEntity i WHERE i.category.profile.id = :profileId ORDER BY i.date DESC LIMIT 1")
    List<IncomeEntity> findTopByProfileIdOrderByDateDesc(@Param("profileId") Long profileId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM IncomeEntity i WHERE i.category.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM IncomeEntity i WHERE i.category.profile.id = :profileId AND i.date BETWEEN :startDate AND :endDate")
    BigDecimal findTotalExpenseByProfileIdAndDateBetween(
        @Param("profileId") Long profileId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT i FROM IncomeEntity i WHERE i.profile.id = :profileId AND i.date BETWEEN :startDate AND :endDate AND LOWER(i.name) LIKE LOWER(concat('%', :keyword, '%'))")
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
        @Param("profileId") Long profileId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("keyword") String keyword,
        Sort sort
    );

    @Query("SELECT i FROM IncomeEntity i WHERE i.category.profile.id = :profileId AND i.date BETWEEN :startDate AND :endDate")
    List<IncomeEntity> findByProfileIdAndDateBetween(
        @Param("profileId") Long profileId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
