package com.example.demo.Util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

public class AESUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());// 创建一个BouncyCastleProvider对象
    }

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";// 算法名称/加密模式/填充方式

    public static String decrypt(String encrypted, String key, String iv)// 解密方法，参数为密文、密钥、偏移量
            throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);// 创建一个Cipher对象
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");// 创建一个SecretKeySpec对象，用于构建密钥
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));// 创建一个IvParameterSpec对象，用于构建iv
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);// 初始化Cipher对象
        byte[] decodedBytes = Base64.getDecoder().decode(encrypted);// 创建一个byte数组，用于存放解密后的数据
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);// 创建一个byte数组，用于存放解密后的数据
        return new String(decryptedBytes, StandardCharsets.UTF_8);// 返回解密后的数据
    }
}
