package com.lab.employee_service;

public class EmployeeMapper {

    public static Employee toEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());
        employee.setRole(employeeDTO.getRole());
        employee.setEmail(employeeDTO.getEmail());
        employee.setDepartmentId(employeeDTO.getDepartmentId());
        return employee;
    }

    public static EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setName(employee.getName());
        employeeDTO.setRole(employee.getRole());
        employeeDTO.setEmail(employee.getEmail());
        employeeDTO.setDepartmentId(employee.getDepartmentId());
        return employeeDTO;
    }
}
