package com.example.demo.Config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {// 验证码配置类

    // 生成验证码的 Bean
    @Bean
    public DefaultKaptcha producer() {

        Properties properties = new Properties();// 验证码属性设置
        properties.put("kaptcha.border", "no");// 是否有边框
        properties.put("kaptcha.textproducer.font.color", "black");// 字体颜色
        properties.put("kaptcha.textproducer.char.space", "5");// 字符间距
        properties.put("kaptcha.image.width", "200");// 图片宽度
        properties.put("kaptcha.image.height", "50");// 图片高度
        properties.put("kaptcha.textproducer.char.length", "4");// 字符个数
        properties.put("kaptcha.textproducer.font.names", "Arial,Courier");// 字体

        Config config = new Config(properties);// 创建配置对象
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();// 创建验证码生成器
        defaultKaptcha.setConfig(config);// 设置配置对象
        return defaultKaptcha;// 返回验证码生成器
    }
}
