package com.example.demo.Entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data // getters and setters
@Table(name = "employee")
public class Employee {
    @Id
    private Integer id;// 员工编号

    private String name;// 员工姓名

    private String male;// 员工性别

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String on_date;// 入职日期

    private String posts;// 员工职位

    private Integer age;// 员工年龄

    private Integer wages;// 员工工资

    @Column(nullable = false, length = 32)
    private String password;// 员工密码

    @Column(length = 32)
    private String salt;// 盐值

}
