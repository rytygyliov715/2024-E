package com.example.demo.Controller;

import com.example.demo.Entity.Employee;
import com.example.demo.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    // 员工名单展示
    @GetMapping("/employees")
    public String viewHomePage(Model model) {
        // 获取所有员工
        model.addAttribute("listEmployees", employeeService.getAllEmployees());
        return "employee/index";// 返回index.html
    }

    // 新增员工
    @GetMapping("/showNewEmployeeForm")
    public String showNewEmployeeForm(Model model) {
        // 创建一个员工对象
        Employee employee = new Employee();
        // 将员工对象添加到模型中
        model.addAttribute("employee", employee);
        return "employee/new_employee";// 返回new_employee.html
    }

    // 保存员工
    @PostMapping("/saveEmployee")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {
        // 保存员工到数据库
        employeeService.saveOrUpdateEmployee(employee);
        return "redirect:/employees";// 完成保存操作后重定向到员工名单页面
    }

    // 更新员工
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Integer id, Model model) {
        // 根据id获取员工
        Employee employee = employeeService.getEmployeeById(id);
        // 将员工添加到模型中
        model.addAttribute("employee", employee);
        return "employee/update_employee";// 前往update_employee.html
    }

    // 删除员工
    @GetMapping("/deleteEmployee/{id}")
    public String deleteEmployee(@PathVariable(value = "id") Integer id) {
        // 删除员工
        this.employeeService.deleteEmployee(id);
        return "redirect:/employees";// 完成删除操作后重定向到员工名单页面
    }

    // excel导入数据
    @GetMapping("/emp_excelimport")
    public ModelAndView importEmployees() {
        return new ModelAndView("employee/emp_excelimport");// 前往emp_excelimport.html
    }

    @PostMapping("/emp_excelimport")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "skipDuplicates", defaultValue = "false") boolean skipDuplicates) {
        try {
            employeeService.importExcel(file, skipDuplicates);// 导入Excel数据
            return ResponseEntity.ok("已成功导入 Excel 数据.");
        } catch (IOException e) {
            // 如果导入失败，返回错误信息
            System.out.println("导入 Excel 数据失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入 Excel 数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "shiro/register";
    }

    @PostMapping("/register")
    public String registerEmployee(@ModelAttribute Employee employee) {
        employeeService.register(employee);
        return "redirect:/login"; // Assuming you have a login page
    }
}
