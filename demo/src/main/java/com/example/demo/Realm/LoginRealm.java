package com.example.demo.Realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.Entity.Employee;
import com.example.demo.Service.EmployeeService;
import org.springframework.stereotype.Component;

@Component
public class LoginRealm extends AuthorizingRealm {

    @Autowired
    private EmployeeService employeeService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 添加角色和权限
        return authorizationInfo;
    }

    // 登录认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)// 获取认证信息
            throws AuthenticationException {
        String id = (String) token.getPrincipal();// 获取用户名
        Employee employee = employeeService.getEmployeeById(Integer.valueOf(id));// 根据用户名获取用户
        if (employee == null) {// 如果输入的用户名或者密码错误
            throw new UnknownAccountException();// 抛出未知用户异常
        }
        return new SimpleAuthenticationInfo(employee, employee.getPassword(), getName());// 返回认证信息
    }

}