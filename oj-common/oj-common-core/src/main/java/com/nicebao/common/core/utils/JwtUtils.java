package com.nicebao.common.core.utils;
import com.nicebao.common.core.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

import static cn.hutool.core.convert.Convert.toStr;

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
	public static Claims parseToken(String token, String secret) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	public static String getUserKey(Claims claims) {
		return toStr(claims.get(JwtConstants.LOGIN_USER_KEY));
	}

	public static String getUserId(Claims claims) {
		return toStr(claims.get(JwtConstants.LOGIN_USER_ID));
	}


	private static String toStr(Object value) {
		if (value == null) {
			return "";
		}
		return value.toString();
	}

}
