package com.example.demo.Controller;

import com.example.demo.Entity.Employee;
import com.example.demo.Service.EmployeeService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private DefaultKaptcha producer;// 注入 DefaultKaptcha 对象,用于生成验证码

    @Autowired
    private EmployeeService employeeService;

    // 登录页面
    @GetMapping("/login")
    public String login() {
        return "shiro/login";
    }

    // 登录处理
    @PostMapping("/login")
    public String login(@RequestParam("id") String id, // 获取用户名
            @RequestParam("password") String password, // 获取密码
            @RequestParam("captcha") String captcha, // 获取验证码
            HttpSession session, // 获取session对象
            Model model) {
        String kaptcha = (String) session.getAttribute("kaptcha");// 获取session中的验证码
        session.setAttribute("id", id);// 将id保存到session中
        if (!captcha.equalsIgnoreCase(kaptcha)) {// 判断验证码是否正确
            model.addAttribute("error", "验证码错误");
            return "shiro/login";
        }
        try {
            Subject subject = SecurityUtils.getSubject();// 获取当前用户
            UsernamePasswordToken token = new UsernamePasswordToken(id, password);// 创建用户名密码令牌
            subject.login(token);// 登录
            return "redirect:/dashboard";
        } catch (AuthenticationException e) {// 如果登录失败
            model.addAttribute("error", "用户名或密码错误");
            return "shiro/login";
        }
    }

    // 主页
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String id = (String) session.getAttribute("id");
        if (id == null) {// 如果session中没有id
            return "redirect:/login";// 重定向到登录页面
        }
        Employee employee = employeeService.getEmployeeById(Integer.valueOf(id)); // 根据id获取员工
        List<Employee> employees = Arrays.asList(employee); // 创建一个只包含这个员工的列表
        model.addAttribute("employees", employees);
        return "shiro/dashboard";
    }

    // 获取验证码
    @GetMapping("/captcha")
    public void captcha(HttpServletResponse response, // HttpServletResponse对象用于设置响应的内容类型和输出流
            HttpSession session)// HttpSession对象用于保存验证码
            throws IOException {// IOException异常用于处理输入输出异常
        try {
            response.setContentType("image/jpeg");// 设置响应的内容类型
            String text = producer.createText();// 生成验证码
            session.setAttribute("kaptcha", text);// 将验证码保存到session中
            BufferedImage image = producer.createImage(text);// 生成验证码图片
            ServletOutputStream out = response.getOutputStream();// 获取输出流
            ImageIO.write(image, "jpg", out);// 输出图片
            out.flush();// 刷新输出流
            out.close();// 关闭输出流
        } catch (IOException e) {
            e.printStackTrace();// 打印异常信息
        }
    }

}
