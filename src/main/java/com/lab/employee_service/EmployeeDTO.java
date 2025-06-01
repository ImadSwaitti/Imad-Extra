package com.lab.employee_service;

import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;
    private String name;
    private String role;
    private String email;
    private Long userId;
}
