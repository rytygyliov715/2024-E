package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordUtil {

    public static String generateSalt() {// 创建一个随机的salt
        SecureRandom random = new SecureRandom();// 生成一个随机数
        byte[] salt = new byte[16];// 创建一个16位的byte数组
        random.nextBytes(salt);// 生成随机数
        return Base64.getEncoder().encodeToString(salt);// 将byte数组转换为字符串
    }

    public static String encryptPassword(String password, String salt) {// 加密密码
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");// 创建一个MD5加密对象
            md.update(salt.getBytes());// 将salt加入到加密对象中
            byte[] bytes = md.digest(password.getBytes());// 将密码加入到加密对象中
            StringBuilder sb = new StringBuilder();// 创建一个字符串，用于存储加密后的密码
            for (byte b : bytes) {// 遍历加密后的byte数组，将其转换为字符串
                sb.append(String.format("%02x", b));// 将byte数组转换为16进制
            }
            return sb.toString();// 返回加密后的密码
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
