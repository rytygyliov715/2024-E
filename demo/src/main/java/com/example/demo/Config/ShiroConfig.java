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

    // 为 RedisManager 创建 Bean
    @Bean
    public RedisManager redisManager() {// RedisManager用于管理与Redis服务器的连接
        RedisManager redisManager = new RedisManager();// 创建 RedisManager 对象
        redisManager.setHost("localhost:6379");// 设置 Redis 服务器地址
        return redisManager;
    }

    // 为 RedisCacheManager 创建 Bean
    @Bean
    public RedisCacheManager cacheManager() {// 缓存管理器，负责创建和管理 RedisCache 实例
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    // 为 RedisSessionDAO 创建 Bean
    @Bean
    public RedisSessionDAO redisSessionDAO() {// RedisSessionDAO用于将会话数据存储到Redis中
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    // 为 SessionManager 创建 Bean
    @Bean
    public DefaultWebSessionManager sessionManager() {// 用于管理会话的生命周期
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();// 创建 DefaultWebSessionManager 对象
        sessionManager.setSessionDAO(redisSessionDAO());// 将redisSessionDAO()设置到sessionManager中
        return sessionManager;
    }

    // 为 SecurityManager 创建 Bean
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();// 创建 DefaultWebSecurityManager 对象
        securityManager.setCacheManager(cacheManager());// 配置缓存管理器
        securityManager.setSessionManager(sessionManager());// 配置会话管理器
        securityManager.setRealm(ShiroRealm());// 配置 Realm
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
