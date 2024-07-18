package com.example.demo.Controller;

import javax.servlet.http.HttpServletResponse;
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
import java.util.Map;
import java.util.List;

@RestController
public class ExportLIMITController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";// 响应内容类型
    private static final String HEADER_KEY = "Content-Disposition";// 响应头的键
    private static final String HEADER_VALUE_PREFIX = "attachment; filename=";// 响应头的值前缀
    private static final String SQL_QUERY_TEMPLATE = "SELECT * FROM %s LIMIT %d, %d";// SQL查询模板

    // 显示导出页面
    @GetMapping("/exportLIMIT")
    public ModelAndView showExportLIMITPage() {
        return new ModelAndView("excel/ExcelExportLIMIT");
    }

    // 导出方法
    @PostMapping("/exportLIMIT")
    public void excelExportLIMIT(@RequestParam(name = "tableName") String tableName, // 接受前端传来的表名
            @RequestParam(name = "page") String page, // 接受前端传来的页数
            @RequestParam(name = "limit") String limit, // 接受前端传来的行数
            HttpServletResponse response) throws Exception {

        try {
            // 验证参数
            int pageNumber = Integer.parseInt(page);
            int limitNumber = Integer.parseInt(limit);

            if (pageNumber <= 0 || limitNumber <= 0) {
                throw new IllegalArgumentException("页面和限制参数必须大于零.");
            }

            // 设置响应内容类型和标题
            response.setContentType(CONTENT_TYPE);
            response.setHeader(HEADER_KEY, HEADER_VALUE_PREFIX + tableName + ".xlsx");

            // 从指定表中获取数据
            String sqlQuery = String.format(SQL_QUERY_TEMPLATE, tableName, (pageNumber - 1) * limitNumber, limitNumber);
            List<Map<String, Object>> data = jdbcTemplate.queryForList(sqlQuery);

            // 创建新的工作簿和工作表
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Sheet1");

                // 创建标题行
                createHeaderRow(data, sheet);

                // 填充数据行
                populateDataRows(data, sheet);

                // 将工作簿写入响应输出流
                workbook.write(response.getOutputStream());
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的页面或限制参数.");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());// 发送错误
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "导出数据时出错.");
        }
    }

    // 创建标题行
    private void createHeaderRow(List<Map<String, Object>> data, Sheet sheet) {// 创建标题行
        if (!data.isEmpty()) {// 如果数据不为空
            Row headerRow = sheet.createRow(0);// 创建行
            int cellIndex = 0;// 列数
            for (String key : data.get(0).keySet()) {// 遍历数据的key
                Cell cell = headerRow.createCell(cellIndex++);// 创建单元格
                cell.setCellValue(key);// 设置单元格的值
            }
        }
    }

    private void populateDataRows(List<Map<String, Object>> data, Sheet sheet) {// 填充数据行
        int rowIndex = 1;// 行数
        for (Map<String, Object> rowData : data) {// 遍历数据
            Row row = sheet.createRow(rowIndex++);// 创建行
            int cellIndex = 0;// 列数
            for (Object value : rowData.values()) {// 遍历数据的值
                Cell cell = row.createCell(cellIndex++);// 创建单元格
                cell.setCellValue(value != null ? value.toString() : "");// 设置单元格的值
            }
        }
    }
}
