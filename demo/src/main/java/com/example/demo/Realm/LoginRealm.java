package com.example.demo.Realm;

import com.example.demo.Entity.Employee;
import com.example.demo.Service.EmployeeService;
import com.example.demo.Util.PasswordUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginRealm extends AuthorizingRealm {

    @Autowired
    private EmployeeService employeeService;

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    // 登录认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String id = (String) token.getPrincipal(); // 获取用户名
        Employee employee = employeeService.getEmployeeById(Integer.valueOf(id)); // 根据用户名获取用户

        if (employee == null) { // 如果输入的用户名或者密码错误
            throw new UnknownAccountException(); // 抛出未知用户异常
        }

        // 使用盐值和加密后的密码进行验证
        String salt = employee.getSalt();// 获取盐值
        String encryptedPassword = PasswordUtil.encryptPassword(token.getCredentials().toString(), salt);// 获取加密后的密码

        // 打印加密后的密码用于验证
        System.out.println("Encrypted Password: " + encryptedPassword);

        return new SimpleAuthenticationInfo(
                employee,
                employee.getPassword(), // 获取密码
                ByteSource.Util.bytes(salt),
                getName());
    }
}