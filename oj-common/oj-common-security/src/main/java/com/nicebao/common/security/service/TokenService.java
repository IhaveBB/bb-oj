package com.nicebao.common.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * TokenService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
@Service
@Slf4j
public class TokenService {
	@Autowired
	private RedisService redisService;
	public String createToken(Long userId, String secret, Integer identity, String nickName, String headImage) {
		Map<String, Object> claims = new HashMap<>();
		String userKey = UUID.fastUUID().toString();
		claims.put(JwtConstants.LOGIN_USER_ID, userId);
		claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
		String token = JwtUtils.createToken(claims, secret);
		String tokenKey = getTokenKey(userKey);
//            String tokenKey = "logintoken:" + sysUser.getUserId();
		LoginUser loginUser = new LoginUser();
		loginUser.setIdentity(identity);
		loginUser.setNickName(nickName);
		loginUser.setHeadImage(headImage);
		redisService.setCacheObject(tokenKey, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);

		return token;
	}
}
