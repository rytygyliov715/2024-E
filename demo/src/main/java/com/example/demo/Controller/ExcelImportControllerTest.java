package com.example.demo.Controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// ExcelImportController 使用 @RestController 注解，表示这是一个Spring MVC控制器。
// 该类映射处理 /test URL的请求。
@RestController
@RequestMapping("/test")
public class ExcelImportControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate; // 自动注入JdbcTemplate用于数据库操作

    // 处理GET请求"/test/excelimportTest"并返回"excelimport"页面
    @GetMapping("/excelimportTest")
    public ModelAndView showExcelImportPage() {
        return new ModelAndView("excel/excelimportTest");
    }

    // 处理POST请求"/test/excelimportTest"，并处理上传的Excel文件
    @PostMapping("/excelimportTest")
    public String handleExcelImport(@RequestParam("tableName") String tableName,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "skipDuplicates", required = false, defaultValue = "false") boolean skipDuplicates) {
        try {
            // 1. 解析Excel文件
            InputStream is = file.getInputStream(); // 获取上传文件的输入流
            Workbook workbook = new XSSFWorkbook(is); // 从输入流创建工作簿
            Sheet sheet = workbook.getSheetAt(0); // 获取工作簿中的第一个工作表
            Row headerRow = sheet.getRow(0); // 获取工作表中的第一行（表头行）

            // 2. 构建SQL查询语句
            StringBuilder baseSql = new StringBuilder("INSERT INTO " + tableName + " (");

            // 将列名添加到SQL查询语句中
            List<String> columns = new ArrayList<>();
            for (Cell cell : headerRow) {
                if (cell.getCellType() == CellType.STRING) {
                    columns.add(cell.getStringCellValue());
                }
            }
            for (String column : columns) {
                baseSql.append(column).append(",");
            }
            baseSql.deleteCharAt(baseSql.length() - 1); // 删除尾随逗号
            baseSql.append(") VALUES (");

            // 然后，对于每一行数据，构建一个新的SQL查询，并执行它
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                StringBuilder valueSql = new StringBuilder();
                StringBuilder updateSql = new StringBuilder("UPDATE " + tableName + " SET ");
                String id = null;

                for (int j = 0; j < headerRow.getPhysicalNumberOfCells(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    String columnName = columns.get(j);

                    if (columnName.equalsIgnoreCase("id")) {
                        // 第一列为id，不更新id列
                        if (cell.getCellType() == CellType.NUMERIC) {
                            id = String.valueOf((int) cell.getNumericCellValue());
                        } else if (cell.getCellType() == CellType.STRING) {
                            id = cell.getStringCellValue();
                        }
                        continue; // 跳过 id 列的处理
                    }

                    // 根据单元格类型处理数据
                    if (cell.getCellType() == CellType.BLANK) {
                        valueSql.append("NULL,");
                        updateSql.append(columnName).append(" = NULL,");
                    } else if (cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue();
                        valueSql.append("'").append(cellValue).append("',");
                        updateSql.append(columnName).append(" = '").append(cellValue).append("',");
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        double numericCellValue = cell.getNumericCellValue();
                        if (numericCellValue == (int) numericCellValue) {
                            valueSql.append((int) numericCellValue).append(",");
                            updateSql.append(columnName).append(" = ").append((int) numericCellValue).append(",");
                        } else {
                            valueSql.append(numericCellValue).append(",");
                            updateSql.append(columnName).append(" = ").append(numericCellValue).append(",");
                        }
                    }
                }

                valueSql.deleteCharAt(valueSql.length() - 1);
                updateSql.deleteCharAt(updateSql.length() - 1);
                updateSql.append(" WHERE id = ?");

                // 3. 检查数据是否已经存在，如果不存在，则插入新数据，否则更新数据
                String checkSql = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";// 检查数据是否存在的SQL查询
                System.out.println("更新语句：" + checkSql.toString());
                int count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);// 查询数据是否存在
                if (count == 0) {
                    System.out.println("插入语句：" + baseSql.toString() + valueSql + ")");
                    jdbcTemplate.execute(baseSql.toString() + valueSql + ")");
                } else {
                    if (skipDuplicates) {
                        continue;
                    } else {
                        jdbcTemplate.update(updateSql.toString(), id);
                        System.out.println("更新语句：" + updateSql.toString());
                    }
                }
            }
            // 关闭工作簿
            workbook.close();
        } catch (Exception e) {
            // 打印异常的堆栈跟踪
            e.printStackTrace();
            // 如果发生异常，返回错误信息
            return "错误: " + e.getMessage();
        }
        // 如果数据导入成功，返回成功信息
        return "数据导入成功";
    }
}
