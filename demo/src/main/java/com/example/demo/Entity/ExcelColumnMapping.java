package com.example.demo.Entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "excel_column_mapping")
public class ExcelColumnMapping {
    @Id
    private Integer id;

    private String table_name;

    private String excel_header;

    private String db_column;
}
