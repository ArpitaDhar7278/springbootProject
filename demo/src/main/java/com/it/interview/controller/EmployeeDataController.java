package com.it.interview.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it.interview.entity.Employee;
import com.it.interview.entity.TaxDeduction;

@RestController
@RequestMapping("/employee/")
public class EmployeeDataController {

	private Map<Integer, Employee> employeeData = new HashMap<>();

	@PostMapping("save")
	public ResponseEntity<?> saveEmployeeDetails(@Valid @RequestBody Employee employee, BindingResult result) {
		if (result.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				errors.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(errors);
		}
		employeeData.put(employee.getEmployeeId(), employee);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("taxdeduction")
	public List<TaxDeduction> getEmployeeTaxDeductions() {
		List<Employee> employees = getEmployees();

		List<TaxDeduction> taxDeductions = new ArrayList<>();

		for (Employee employee : employees) {
			double yearlySalary = calcYearlySalary(employee);
			double taxAmount = calcTaxAmount(yearlySalary);
			double cessAmount = calcCessAmount(yearlySalary);

			TaxDeduction taxDeduction = new TaxDeduction(employee.getEmployeeId(), employee.getFirstName(),
					employee.getLastName(), yearlySalary, taxAmount, cessAmount);

			taxDeductions.add(taxDeduction);
		}

		return taxDeductions;
	}

	private double calcYearlySalary(Employee employee) {
		LocalDate financialYearStart = LocalDate.of(2023, 4, 1);
		LocalDate financialYearEnd = LocalDate.of(2024, 3, 31);
		double monthlySalary = employee.getSalary();
		LocalDate joinDate = employee.getDateOfJoining();
		if (financialYearStart.isBefore(joinDate)) {
			financialYearStart = joinDate;
		}
		long months = ChronoUnit.MONTHS.between(financialYearStart, financialYearEnd) + 1;
		double lossOfPayPerDay = monthlySalary / 30;
		double salary = (monthlySalary * months)
				- (lossOfPayPerDay * getLossOfPayDaysData(financialYearStart, financialYearEnd, joinDate));
		return salary;
	}

	private int getLossOfPayDaysData(LocalDate start, LocalDate end, LocalDate joinDate) {
		int lossOfPayDays = 0;
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			if (date.isBefore(joinDate) || date.isEqual(joinDate)) {
				lossOfPayDays++;
			}
		}
		return lossOfPayDays;
	}

	private double calcTaxAmount(double yearlySalary) {
		double taxAmount = 0;

		if (yearlySalary <= 250000) {
			taxAmount = 0;
		} else if (yearlySalary > 250000 && yearlySalary <= 500000) {
			taxAmount = (yearlySalary - 250000) * 0.05;
		} else if (yearlySalary > 500000 && yearlySalary <= 1000000) {
			taxAmount = 12500 + (yearlySalary - 500000) * 0.1;
		} else if (yearlySalary > 1000000) {
			taxAmount = 87500 + (yearlySalary - 1000000) * 0.2;
		}

		return taxAmount;
	}

	private double calcCessAmount(double yearlySalary) {
		double cessAmount = 0;
		if (yearlySalary > 2500000) {
			double taxableAmount = yearlySalary - 2500000;
			cessAmount = taxableAmount * 0.02;
		}
		return cessAmount;
	}

	private List<Employee> getEmployees() {
		List<Employee> employees = new ArrayList<>();
		employees.add(
				new Employee(1, "Arpita", "Dhar", "arpita@gmail.com", "9876543210", LocalDate.of(2022, 6, 1), 250000));
		employees.add(
				new Employee(2, "Ankita", "Sahu", "ankita@gmail.com", "0987654321", LocalDate.of(2023, 1, 10), 60000));

		return employees;
	}

}
