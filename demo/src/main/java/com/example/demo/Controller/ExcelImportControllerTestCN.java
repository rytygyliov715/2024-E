package com.example.demo.Controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class ExcelImportControllerTestCN {

    @Autowired
    private JdbcTemplate jdbcTemplate;// 注入JdbcTemplate对象

    @GetMapping("/excelimportTestCN")
    public ModelAndView showExcelImportPage() {
        return new ModelAndView("excel/excelimportTestCN");
    }

    @PostMapping("/excelimportTestCN")
    public String handleExcelImport(@RequestParam("tableName") String tableName,

            @RequestParam("file") MultipartFile file, // 上传的文件
            @RequestParam(value = "skipDuplicates", required = false, defaultValue = "false") boolean skipDuplicates) {// 是否跳过重复数据
        InputStream is = null;// 输入流
        Workbook workbook = null;// 工作簿对象
        try {
            is = file.getInputStream();// 获取上传文件的输入流
            workbook = new XSSFWorkbook(is);// 创建工作簿对象
            Sheet sheet = workbook.getSheetAt(0);// 获取第一个工作表
            Row headerRow = sheet.getRow(0);// 获取标题行

            // 获取Excel标题和数据库列名的映射关系
            Map<String, String> columnMapping = getColumnMapping(tableName);

            // 构建数据库列名列表
            List<String> dbColumns = new ArrayList<>();// 用于存储数据库列名
            List<String> excelHeaders = new ArrayList<>();// 用于存储Excel标题
            for (Cell cell : headerRow) {// 遍历标题行中的单元格
                if (cell.getCellType() == CellType.STRING) {// 如果单元格类型为字符串
                    String excelHeader = cell.getStringCellValue();// 获取单元格的字符串值
                    if (columnMapping.containsKey(excelHeader)) {// 如果标题在映射关系中
                        dbColumns.add(columnMapping.get(excelHeader));//
                        excelHeaders.add(excelHeader);// 将标题和列名存储到列表中
                    }
                }
            }

            if (dbColumns.isEmpty()) {// 如果数据库列名列表为空
                throw new RuntimeException("在 Excel 标题行中找不到有效列.");
            }

            // 构建基础的SQL查询语句
            StringBuilder baseSql = new StringBuilder("INSERT INTO " + tableName + " (");
            for (String dbColumn : dbColumns) {// 遍历数据库列名列表
                baseSql.append(dbColumn).append(",");// 拼接SQL语句中的列名部分
            }
            baseSql.deleteCharAt(baseSql.length() - 1);// 删除最后一个逗号
            baseSql.append(") VALUES (");// 拼接SQL语句中的VALUES部分

            // 遍历Excel数据行
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);// 获取行对象
                StringBuilder valueSql = new StringBuilder();// 用于拼接SQL语句中的值部分
                StringBuilder updateSql = new StringBuilder("UPDATE " + tableName + " SET ");// 用于拼接SQL语句中的更新部分
                String id = null;// 用于存储id值
                boolean rowHasValues = false;// 用于标记行中是否有有效数据

                // 遍历行中的单元格
                for (int j = 0; j < excelHeaders.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);// 获取单元格对象
                    String excelHeader = excelHeaders.get(j);// 获取单元格对应的Excel标题
                    String dbColumn = columnMapping.get(excelHeader);// 获取单元格对应的数据库列名

                    if (dbColumn.equalsIgnoreCase("id")) {// 如果是id列
                        if (cell.getCellType() == CellType.NUMERIC) {// 如果单元格类型为数字
                            id = String.valueOf((int) cell.getNumericCellValue());// 获取id值
                            valueSql.append(id).append(",");// 拼接SQL语句中的值部分
                        } else if (cell.getCellType() == CellType.STRING) {// 如果单元格类型为字符串
                            id = cell.getStringCellValue();// 获取id值
                            valueSql.append("'").append(id).append("',");// 拼接SQL语句中的值部分
                        }
                    } else {
                        if (cell.getCellType() == CellType.BLANK) {// 如果单元格类型为空白
                            valueSql.append("NULL,");// 将NULL值拼接到SQL语句中
                            updateSql.append(dbColumn).append(" = NULL,");// 将NULL值拼接到更新语句中
                        } else if (cell.getCellType() == CellType.STRING) {// 如果单元格类型为字符串
                            String cellValue = cell.getStringCellValue();// 获取单元格的字符串值
                            valueSql.append("'").append(cellValue).append("',");// 将字符串值拼接到SQL语句中
                            updateSql.append(dbColumn).append(" = '").append(cellValue).append("',");// 将字符串值拼接到更新语句中
                            rowHasValues = true;// 标记行中有有效数据
                        } else if (cell.getCellType() == CellType.NUMERIC) {// 如果单元格类型为数字
                            double numericCellValue = cell.getNumericCellValue();// 获取单元格的数字值
                            if (numericCellValue == (int) numericCellValue) {// 如果是整数
                                valueSql.append((int) numericCellValue).append(",");
                                updateSql.append(dbColumn).append(" = ").append((int) numericCellValue).append(",");
                            } else {// 如果是小数
                                valueSql.append(numericCellValue).append(",");
                                updateSql.append(dbColumn).append(" = ").append(numericCellValue).append(",");
                            }
                            rowHasValues = true;// 标记行中有有效数据
                        }
                    }
                }

                if (id == null || !rowHasValues) {
                    continue; // 如果没有id或行中没有有效数据，则跳过该行
                }

                valueSql.deleteCharAt(valueSql.length() - 1);// 删除最后一个逗号
                updateSql.deleteCharAt(updateSql.length() - 1);
                updateSql.append(" WHERE id = ?");// 拼接更新语句的WHERE条件

                String checkSql = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";// 查询是否存在该id的记录
                int count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);// 获取记录数
                if (count == 0) {// 如果不存在该id的记录
                    jdbcTemplate.execute(baseSql.toString() + valueSql + ")");// 执行插入语句
                } else {
                    if (skipDuplicates) {
                        continue;// 如果跳过重复数据，则跳过该行
                    } else {
                        // 如果不跳过重复数据，则执行更新语句
                        jdbcTemplate.update(updateSql.toString(), id);
                        System.out.println("更新语句：" + updateSql.toString());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "错误: " + e.getMessage();
        } finally {
            if (workbook != null) {
                try {
                    // 关闭工作簿
                    workbook.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
        return "数据导入成功";
    }

    // 获取Excel标题和数据库列名的映射关系
    private Map<String, String> getColumnMapping(String tableName) {
        // 查询数据库中的映射关系
        String sql = "SELECT excel_header, db_column FROM excel_column_mapping WHERE table_name = ?";
        // 将查询结果转换为Map对象
        List<Map<String, Object>> mappings = jdbcTemplate.queryForList(sql, tableName);
        // 创建一个Map对象用于存储映射关系
        Map<String, String> columnMapping = new HashMap<>();
        // 遍历查询结果，将映射关系存储到Map对象中
        for (Map<String, Object> mapping : mappings) {
            columnMapping.put((String) mapping.get("excel_header"), (String) mapping.get("db_column"));
        }
        // 返回映射关系
        return columnMapping;
    }
}
