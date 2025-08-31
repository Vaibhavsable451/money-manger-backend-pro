package com.vaibhav.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhav.dto.ExpenseDTO;
import com.vaibhav.dto.FilterDTO;
import com.vaibhav.dto.IncomeDTO;
import com.vaibhav.service.ExpenseService;
import com.vaibhav.service.IncomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
@Slf4j
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter) {
        try {
            log.info("Received filter request: {}", filter);
            
            // Set default values if not provided
            LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.of(1970, 1, 1);
            LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
            String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
            String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
            Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortField);

            log.debug("Processed filter - startDate: {}, endDate: {}, keyword: {}, sort: {}", 
                    startDate, endDate, keyword, sort);

            if (filter.getType() == null || filter.getType().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Type parameter is required");
            }

            if ("income".equalsIgnoreCase(filter.getType())) {
                List<IncomeDTO> result = incomeService.filterIncome(startDate, endDate, keyword, sort);
                log.info("Found {} income records", result.size());
                return ResponseEntity.ok(result);
            } else if ("expense".equalsIgnoreCase(filter.getType())) {
                List<ExpenseDTO> result = expenseService.filterExpense(startDate, endDate, keyword, sort);
                log.info("Found {} expense records", result.size());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'");
            }
        } catch (Exception e) {
            log.error("Error processing filter request", e);
            return ResponseEntity.internalServerError().body("An error occurred while processing your request");
        }
    }
}
