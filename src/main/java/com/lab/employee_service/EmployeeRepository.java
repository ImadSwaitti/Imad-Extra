package com.lab.employee_service;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    public Optional<Employee> findByEmail(String email);
}