package com.it.interview.entity;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

	@Positive(message = "Employee ID must be a positive integer")
	private int employeeId;

	@NotBlank(message = "First Name is mandatory")
	private String firstName;

	@NotBlank(message = "Last Name is mandatory")
	private String lastName;

	@Email(message = "Invalid Email format")
	private String email;

	@NotBlank(message = "Phone Number is mandatory")
	private String mobileNumber;

	@NotNull(message = "Date of Joining is mandatory")
	private LocalDate dateOfJoining;

	@Positive(message = "Salary must be a positive number")
	private double salary;

}
