package com.lab.employee_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "department-service")
public interface DepartmentClient {

    @GetMapping("/departments/{id}")
    EntityModel<DepartmentDto> one(@PathVariable Long id);

}
