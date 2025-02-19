package com.nicebao.common.core.utils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

/**
 * JwtUtils
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
public class JwtUtils {
	/**
	 * 生成令牌
	 * @author IhavBB
	 * @date 17:44 2025/2/19
	 * @param claims
	 * @param secret
	 * @return {@link String}
	**/
	public static String createToken(Map<String, Object> claims, String secret) {
		String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
		return token;
	}

}
