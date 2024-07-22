package com.example.demo.Config;

import com.example.demo.Realm.LoginRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {

    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost("localhost:6379");
        return redisManager;
    }

    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        return sessionManager;
    }

    // 为 SecurityManager 创建 Bean
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setCacheManager(cacheManager());
        securityManager.setSessionManager(sessionManager());
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

    // 使用 Shiro 的内置加盐支持
    // 配置 `HashedCredentialsMatcher` 使用 MD5 并启用加盐:
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {// 创建一个 HashedCredentialsMatcher 方法
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();// 创建一个
                                                                                           // HashedCredentialsMatcher
                                                                                           // 对象
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");// 设置加密算法为 MD5
        hashedCredentialsMatcher.setHashIterations(1024);// 设置加密次数为 1024
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);// 设置加密后的密码为十六进制
        return hashedCredentialsMatcher;// 返回 HashedCredentialsMatcher 对象
    }
}
