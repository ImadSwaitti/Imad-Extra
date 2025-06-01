package com.lab.employee_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.IanaLinkRelations.SELF;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.lab.employee_service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

class EmployeeServiceImplTest {

    @InjectMocks
    private EmployeeServiceImpl service;

    @Mock
    private EmployeeRepository repository;

    @Mock
    private EmployeeModelAssembler assembler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Employee emp = new Employee("Alice", "Developer", "alice@example.com");
        emp.setId(1L);

        when(repository.findAll()).thenReturn(List.of(emp));

        EmployeeDTO dto = EmployeeMapper.toDTO(emp);
        EntityModel<EmployeeDTO> model = EntityModel.of(dto);
        when(assembler.toModel(dto)).thenReturn(model);

        CollectionModel<EntityModel<EmployeeDTO>> result = service.findAll();
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testNewEmployee() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setName("Bob");
        dto.setRole("Manager");
        dto.setEmail("bob@example.com");

        Employee entity = EmployeeMapper.toEntity(dto);
        Employee savedEntity = new Employee("Bob", "Manager", "bob@example.com");
        savedEntity.setId(10L);
        when(repository.save(any(Employee.class))).thenReturn(savedEntity);

        EntityModel<EmployeeDTO> model = EntityModel.of(EmployeeMapper.toDTO(savedEntity));
        model.add(linkTo(EmployeeController.class).slash(10).withSelfRel());

        when(assembler.toModel(any(EmployeeDTO.class))).thenReturn(model);

        ResponseEntity<?> response = service.newEmployee(dto);

        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void testFindById_ValidId() {
        Employee emp = new Employee("John", "Tester", "john@example.com");
        emp.setId(5L);
        when(repository.findById(5L)).thenReturn(Optional.of(emp));

        EmployeeDTO dto = EmployeeMapper.toDTO(emp);
        EntityModel<EmployeeDTO> model = EntityModel.of(dto);
        when(assembler.toModel(dto)).thenReturn(model);

        ResponseEntity<?> result = service.findById(5L);
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void testFindById_NotFound() {
        when(repository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(100L));
    }

    @Test
    void testFindByEmail_Valid() {
        Employee emp = new Employee("Lina", "Engineer", "lina@example.com");
        emp.setId(20L);
        when(repository.findByEmail("lina@example.com")).thenReturn(Optional.of(emp));

        EmployeeDTO dto = EmployeeMapper.toDTO(emp);
        EntityModel<EmployeeDTO> model = EntityModel.of(dto);
        when(assembler.toModel(dto)).thenReturn(model);

        EntityModel<EmployeeDTO> result = service.findByEmail("lina@example.com");
        assertEquals("Lina", result.getContent().getName());
    }

    @Test
    void testFindByEmail_NotFound() {
        when(repository.findByEmail("x@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByEmail("x@example.com"));
    }

    @Test
    void testSave_UpdateExisting() {
        Employee existing = new Employee("Old", "Intern", "old@example.com");
        existing.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        EmployeeDTO updateDTO = new EmployeeDTO();
        updateDTO.setName("New");
        updateDTO.setRole("Lead");
        updateDTO.setEmail("new@example.com");

        Employee updated = new Employee("New", "Lead", "new@example.com");
        updated.setId(1L);
        when(repository.save(any(Employee.class))).thenReturn(updated);

        EntityModel<EmployeeDTO> model = EntityModel.of(EmployeeMapper.toDTO(updated));
        model.add(linkTo(EmployeeController.class).slash(1).withSelfRel());

        when(assembler.toModel(any(EmployeeDTO.class))).thenReturn(model);

        ResponseEntity<?> response = service.save(updateDTO, 1L);
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void testSave_NewWhenNotExists() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        EmployeeDTO newDTO = new EmployeeDTO();
        newDTO.setName("Sara");
        newDTO.setRole("Analyst");
        newDTO.setEmail("sara@example.com");

        Employee newEntity = EmployeeMapper.toEntity(newDTO);
        newEntity.setId(2L);
        when(repository.save(any(Employee.class))).thenReturn(newEntity);

        EntityModel<EmployeeDTO> model = EntityModel.of(EmployeeMapper.toDTO(newEntity));
        model.add(linkTo(EmployeeController.class).slash(2).withSelfRel());

        when(assembler.toModel(any(EmployeeDTO.class))).thenReturn(model);

        ResponseEntity<?> response = service.save(newDTO, 2L);
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void testDeleteById() {
        doNothing().when(repository).deleteById(1L);

        ResponseEntity<?> response = service.deleteById(1L);
        assertEquals(204, response.getStatusCodeValue());
    }
}
