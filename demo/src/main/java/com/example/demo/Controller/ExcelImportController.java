package com.example.demo.Controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;

// ExcelImportController 使用 @RestController 注解，表示这是一个Spring MVC控制器。
// 该类映射处理 /test URL的请求。
@RestController
@RequestMapping("/test")
public class ExcelImportController {

    @Autowired
    private JdbcTemplate jdbcTemplate; // 自动注入JdbcTemplate用于数据库操作

    // 处理GET请求"/test/excelimport"并返回"excelimport"页面
    @GetMapping("/excelimport")
    public ModelAndView showExcelImportPage() {
        return new ModelAndView("excel/excelimport");
    }

    // 处理POST请求"/test/excelimport"，并处理上传的Excel文件
    @PostMapping("/excelimport")
    public String handleExcelImport(@RequestParam("tableName") String tableName,
            @RequestParam("file") MultipartFile file) {
        try {
            // 1. 解析Excel文件
            InputStream is = file.getInputStream(); // 获取上传文件的输入流
            Workbook workbook = new XSSFWorkbook(is); // 从输入流创建工作簿
            Sheet sheet = workbook.getSheetAt(0); // 获取工作簿中的第一个工作表
            Row headerRow = sheet.getRow(0); // 获取工作表中的第一行（表头行）

            // 2. 构建SQL查询语句
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");

            // 将列名添加到SQL查询语句中
            for (Cell cell : headerRow) {
                if (cell.getCellType() == CellType.STRING) {
                    sql.append(cell.getStringCellValue()).append(",");
                }
            }
            sql.deleteCharAt(sql.length() - 1); // 删除尾随逗号
            sql.append(") VALUES (");

            String baseSql = sql.toString(); // 保存基础SQL部分

            // 然后，对于每一行数据，构建一个新的SQL查询，并执行它
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                StringBuilder valueSql = new StringBuilder();
                for (int j = 0; j < headerRow.getPhysicalNumberOfCells(); j++) {
                    // 通过将 Row.MissingCellPolicy.CREATE_NULL_AS_BLANK 传递给 row.getCell(j)
                    // 方法，确保即使单元格为空，也会创建一个空白单元格，从而确保每一行的单元格数与列数一致。
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (cell.getCellType() == CellType.BLANK) {
                        // 对于空单元格，插入NULL
                        valueSql.append("NULL,");
                    } else if (cell.getCellType() == CellType.STRING) {
                        // 对于字符串单元格，插入字符串值
                        valueSql.append("'").append(cell.getStringCellValue()).append("',");
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        // 对于数值单元格，插入数值
                        double numericCellValue = cell.getNumericCellValue();
                        if (numericCellValue == (int) numericCellValue) {
                            // 如果数值是整数，插入整数值
                            valueSql.append((int) numericCellValue).append(",");
                        } else {
                            // 如果数值是浮点数，插入浮点数值
                            valueSql.append(numericCellValue).append(",");
                        }
                    }
                }
                valueSql.deleteCharAt(valueSql.length() - 1); // 删除尾随逗号
                jdbcTemplate.execute(baseSql + valueSql.toString() + ")");
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
