package com.lab.employee_service;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Employee {

  private @Id @GeneratedValue Long id;
  private String name;
  private String role;
  private String email;
  //foregin key
  private Long departmentId;
  //foregin key
  private Long userId;

  public Employee() {
  }
  public Employee(String name, String role, String email) {
    this.name = name;
    this.role = role;
    this.email = email;
  }

}
