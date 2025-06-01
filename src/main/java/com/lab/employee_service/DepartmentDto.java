package com.lab.employee_service;

import lombok.Data;

@Data
public class DepartmentDto {
    private Long id;
    private String name;
    private String location;

    public DepartmentDto() {}

    public DepartmentDto(String name, String location) {
        this.name = name;
        this.location = location;
    }

}