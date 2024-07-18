package com.example.demo.Config;

import com.example.demo.Realm.LoginRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    // 为 SecurityManager 创建 Bean
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(ShiroRealm());
        ThreadContext.bind(securityManager); // 手动绑定 SecurityManager
        return securityManager;
    }

    // 为 LoginRealm 创建一个 Bean
    @Bean
    public LoginRealm ShiroRealm() {
        return new LoginRealm();
    }

    // 为 ShiroFilterFactoryBean 创建一个 bean
    // @Bean
    // public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
    // ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    // shiroFilterFactoryBean.setSecurityManager(securityManager);
    // shiroFilterFactoryBean.setLoginUrl("/login");
    // shiroFilterFactoryBean.setSuccessUrl("/dashboard");
    // shiroFilterFactoryBean.setUnauthorizedUrl("/403");

    // // 定义过滤器链定义映射
    // Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
    // filterChainDefinitionMap.put("/login", "anon");
    // filterChainDefinitionMap.put("/captcha", "anon");
    // filterChainDefinitionMap.put("/logout", "logout");
    // filterChainDefinitionMap.put("/**", "authc");

    // shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    // return shiroFilterFactoryBean;
    // }
}
