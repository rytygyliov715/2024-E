package com.example.demo.Service;

import com.example.demo.Entity.Employee;
import com.example.demo.Entity.ExcelColumnMapping;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.ExcelColumnMappingRepository;
import com.example.demo.Util.PasswordUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ExcelColumnMappingRepository excelColumnMappingRepository;

    // 获取所有员工
    public List<Employee> getAllEmployees() {

        return employeeRepository.findAll();
    }

    // 根据id获取员工
    public Employee getEmployeeById(Integer id) {

        return employeeRepository.findById(id).orElse(null);
    }

    // 保存或更新员工
    public Employee saveOrUpdateEmployee(Employee employee) {

        return employeeRepository.save(employee);
    }

    // 删除员工
    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    // excel导入数据
    public void importExcel(MultipartFile file, boolean skipDuplicates)
            throws IOException {
        List<Employee> employees = new ArrayList<>(); // 创建list集合用于保存从Excel中读取的数据
        Map<String, String> columnMapping = getColumnMapping(); // 获取列映射

        // 读取Excel文件
        try (InputStream inputStream = file.getInputStream(); // 创建输入流对象
                Workbook workbook = new XSSFWorkbook(inputStream)) { // 创建工作簿对象

            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            Row headerRow = sheet.getRow(0); // 获取标题行

            for (Row currentRow : sheet) { // 判断是否有下一行
                if (currentRow.getRowNum() == 0) { // 如果是标题行，则跳过
                    continue;
                }

                Employee employee = new Employee(); // 创建员工对象
                for (Cell cell : currentRow) { // 遍历每一个单元格
                    String header = headerRow.getCell(cell.getColumnIndex()).getStringCellValue(); // 获取标题行的单元格的值
                    String fieldName = columnMapping.get(header); // 获取列映射中的值

                    // 根据列名设置员工对象的属性
                    // 使用反射机制，根据列名设置员工对象的属性
                    if (fieldName != null) { // 如果列名不为空
                        Object value = getCellValue(cell); // 获取单元格的值
                        setFieldValue(employee, fieldName, value); // 设置员工对象的属性
                    }
                }

                // 如果skipDuplicates为true，且数据库中已存在相同id的员工，则跳过
                if (skipDuplicates && employeeRepository.existsById(employee.getId())) {
                    continue;
                }

                employees.add(employee); // 将员工对象添加到list集合中
            } // try-with-resources已经确保自动关闭资源
        } catch (IOException e) {
            // 处理输入输出异常
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            // 处理其他异常
            e.printStackTrace();
        }

        employeeRepository.saveAll(employees); // 保存员工数据到数据库
    }

    // 获取单元格的值
    private Object getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();// 如果是字符串类型，返回字符串
            // 如果是数字类型，返回int类型
            case NUMERIC ->
                cell.getCellType() == CellType.NUMERIC ? (int) cell.getNumericCellValue() : cell.getDateCellValue();// 如果是日期类型，返回日期
            case BOOLEAN -> cell.getBooleanCellValue();// 如果是布尔类型，返回布尔类型
            default -> null;
        };
    }

    // 设置实体类的字段值
    private void setFieldValue(Employee employee, String fieldName, Object value) {
        try {
            Field field = Employee.class.getDeclaredField(fieldName); // 获取字段
            field.setAccessible(true); // 设置可访问
            if (field.getType().equals(Integer.class)) {
                field.set(employee, Integer.parseInt(value.toString())); // 设置Integer类型的值
            } else if (field.getType().equals(Date.class)) {
                field.set(employee, (Date) value); // 设置Date类型的值
            } else if (field.getType().equals(String.class)) {
                field.set(employee, value.toString()); // 设置String类型的值
            } else if (value == null) {// 如果单元格是空的，设置为null
                field.set(employee, null);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace(); // 处理异常
        }
    }

    // 获取列映射
    private Map<String, String> getColumnMapping() {
        // 正确地从excelColumnMappingRepository获取所有ExcelColumnMapping对象的列表
        List<ExcelColumnMapping> mappings = excelColumnMappingRepository.findAll();
        // 将列表转换为Map，其中键是Excel中的列标题，值是数据库中的列名
        return mappings.stream()
                .filter(mapping -> "Employee".equals(mapping.getTable_name()))
                .collect(Collectors.toMap(ExcelColumnMapping::getExcel_header, ExcelColumnMapping::getDb_column));
    }

    // 注册用户并加密保存密码
    public void register(Employee employee) {
        String salt = PasswordUtil.generateSalt();
        String encryptedPassword = PasswordUtil.encryptPassword(employee.getPassword(), salt);
        employee.setSalt(salt);
        employee.setPassword(encryptedPassword);
        employeeRepository.save(employee);
    }
}
