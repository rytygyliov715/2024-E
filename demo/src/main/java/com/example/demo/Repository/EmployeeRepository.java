package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // 这里不需要写任何方法，Spring Data JPA会自动根据方法名生成SQL语句
}