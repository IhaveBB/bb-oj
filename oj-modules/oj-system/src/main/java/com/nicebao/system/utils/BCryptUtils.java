package com.nicebao.system.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 加密算法工具类
 */
public class BCryptUtils {
    /**
     * 生成加密后密文
     * @author IhavBB
     * @date 16:06 2025/2/19
     * @param password 密码
     * @return {@link String}
    **/
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     * @author IhavBB
     * @date 16:07 2025/2/19
     * @param rawPassword  真实密码
     * @param encodedPassword 加密厚的密码
     * @return {@link boolean}
    **/
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
