package com.example.demo.Realm;

import com.example.demo.Entity.Employee;
import com.example.demo.Service.EmployeeService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
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
        String id = (String) token.getPrincipal(); // 获取用户名，即员工ID
        String password = new String((char[]) token.getCredentials()); // 获取用户输入的密码

        // 使用EmployeeService的login方法进行身份验证
        Employee employee = employeeService.login(id, password);

        if (employee == null) { // 如果身份验证失败
            throw new AuthenticationException("用户名或密码错误");
        }

        // 如果身份验证成功，返回一个AuthenticationInfo实现
        return new SimpleAuthenticationInfo(
                employee,
                password, // 用户输入的密码
                ByteSource.Util.bytes(employee.getSalt()), // 盐值
                getName());
    }
}