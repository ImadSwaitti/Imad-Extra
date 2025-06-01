package com.lab.employee_service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lab.employee_service.exceptions.ResourceNotFoundException;

import feign.FeignException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository repository;
    @Autowired
    private EmployeeModelAssembler assembler;
    @Autowired
    private DepartmentClient departmentClient;

    @Override
    public CollectionModel<EntityModel<EmployeeDTO>> findAll() {
        List<EntityModel<EmployeeDTO>> employees = repository.findAll().stream() //
                .map(EmployeeMapper::toDTO) //
                .map(assembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());

    }

    @Override
    public ResponseEntity<?> newEmployee(EmployeeDTO newEmployee) {
        ResponseEntity<?> validationResponse = validateDepartment(newEmployee.getDepartmentId());
        if (validationResponse != null) {
            return validationResponse;
        }

        Employee employee = EmployeeMapper.toEntity(newEmployee);
        employee = repository.save(employee);
        EntityModel<EmployeeDTO> entityModel = assembler.toModel(EmployeeMapper.toDTO(employee));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        Employee employee = repository.findById(id) //
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + id + " not found."));

        return ResponseEntity.ok(assembler.toModel(EmployeeMapper.toDTO(employee)));
    }

    @Override
    public EntityModel<EmployeeDTO> findByEmail(String email) {
        Employee employee = repository.findByEmail(email) //
                .orElseThrow(() -> new ResourceNotFoundException("Employee with EMAIL " + email + " not found."));
        return assembler.toModel(EmployeeMapper.toDTO(employee));
    }

    @Override
    public ResponseEntity<?> save(EmployeeDTO newEmployee, Long id) {
        ResponseEntity<?> validationResponse = validateDepartment(newEmployee.getDepartmentId());
        if (validationResponse != null) {
            return validationResponse;
        }
        Employee newEmploye = EmployeeMapper.toEntity(newEmployee);
        Employee updatedEmployee = repository.findById(id) //
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    employee.setEmail(newEmployee.getEmail());
                    employee.setDepartmentId(newEmployee.getDepartmentId());
                    return repository.save(employee);
                }) //
                .orElseGet(() -> {
                    return repository.save(newEmploye);
                });

        EntityModel<EmployeeDTO> entityModel = assembler.toModel(EmployeeMapper.toDTO(updatedEmployee));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    @Override
    public ResponseEntity<?> deleteById(Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> validateDepartment(Long departmentId) {
        try {
            departmentClient.one(departmentId).getContent();
            return null; // No error, validation passed
        } catch (FeignException e) {
            log.error("Error calling department-service: {}", e.getMessage());
            if (e.status() == 404) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Department not found");
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Department service is unavailable");
        }
    }

}
