package com.example.demo.Controller;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
public class ExportController {

    @Autowired
    private JdbcTemplate jdbcTemplate;// 注入 JdbcTemplate

    // 显示导出页面
    @GetMapping("/export")
    public ModelAndView showExportPage() {
        return new ModelAndView("excel/ExcelExport");
    }

    // 导出数据到 Excel
    @PostMapping("/export")
    public void exportToExcel(@RequestParam(name = "tableName") String tableName, HttpServletResponse response)// 接受前端传来的表名
            throws IOException, SQLException {
        // 设置响应的内容类型和标头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + tableName + ".xlsx");
        Workbook workbook = null;
        try {
            // 从指定的表中获取数据
            List<Map<String, Object>> data = jdbcTemplate.queryForList("SELECT * FROM " + tableName);

            // 创建新的工作簿和工作表
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet1");

            // 创建标题行
            if (!data.isEmpty()) {// 如果数据不为空
                Row headerRow = sheet.createRow(0);// 创建行
                Map<String, Object> firstRow = data.get(0);// 获取第一行数据
                int headerCol = 0;// 列数

                for (String columnName : firstRow.keySet()) {// 遍历列名
                    Cell cell = headerRow.createCell(headerCol++);// 创建单元格
                    cell.setCellValue(columnName);// 设置单元格的值
                }

                // 填充数据行
                int rowIdx = 1;// 行数
                for (Map<String, Object> rowData : data) {// 遍历数据
                    Row row = sheet.createRow(rowIdx++);// 创建行
                    int colIdx = 0;// 列数
                    for (Object value : rowData.values()) {// 遍历值
                        Cell cell = row.createCell(colIdx++);// 创建单元格
                        if (value != null) {// 如果值不为空
                            cell.setCellValue(value.toString());// 设置单元格的值
                        }
                    }
                }
            }

            // 将工作簿写入响应输出流
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        } finally {
            // 关闭工作流
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response.getOutputStream() != null) {
                try {
                    response.getOutputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}