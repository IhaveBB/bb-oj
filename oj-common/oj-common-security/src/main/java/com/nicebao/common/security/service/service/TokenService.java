package com.nicebao.common.security.service.service;

import cn.hutool.core.lang.UUID;
import com.nicebao.common.core.constants.CacheConstants;
import com.nicebao.common.core.constants.JwtConstants;
import com.nicebao.common.core.domain.LoginUser;
import com.nicebao.common.core.utils.JwtUtils;
import com.nicebao.common.redis.service.RedisService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.convert.Convert.toStr;


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
	/**
	 * JWT中只存用户ID和生成的随机KEY
	 * 随机KEY作为REDIS的主键、方便JWT去REDIS匹配信息
	 * REDIS中存储敏感信息：身份、昵称、头像等等
	 * @author IhavBB
	 * @date 21:54 2025/2/20
	 * @param userId
	 * @param secret
	 * @param identity
	 * @param nickName
	 * @param headImage
	 * @return {@link String}
	**/
	public String createToken(Long userId, String secret, Integer identity, String nickName, String headImage) {
		Map<String, Object> claims = new HashMap<>();
		String userKey = UUID.fastUUID().toString();
		claims.put(JwtConstants.LOGIN_USER_ID, userId);
		claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
		String token = JwtUtils.createToken(claims, secret);

		String tokenKey = getTokenKey(userKey);
		LoginUser loginUser = new LoginUser();
		loginUser.setIdentity(identity);
		loginUser.setNickName(nickName);
		loginUser.setHeadImage(headImage);
		redisService.setCacheObject(tokenKey, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);

		return token;
	}

	/**
	 * 延长令牌有效期
	 * 在身份认证通过之后才会调用的，并且在请求到达controller层之前  在拦截器中调用
	 * @author IhavBB
	 * @date 9:42 2025/2/21
	 * @param claims
	**/
	public void extendToken(Claims claims) {
		String userKey = getUserKey(claims);
		if (userKey == null) {
			return;
		}
		String tokenKey = getTokenKey(userKey);
		//720min  12个小时      剩余  180min 时候对它进行延长
		Long expire = redisService.getExpire(tokenKey, TimeUnit.MINUTES);
		if (expire != null && expire < CacheConstants.REFRESH_TIME) {
			redisService.expire(tokenKey, CacheConstants.EXP, TimeUnit.MINUTES);
		}
	}


	/**
	 * 刷新用户信息
	 * @author IhavBB
	 * @date 23:15 2025/2/20
	 * @param nickName
	 * @param headImage
	 * @param userKey
	**/
	public void refreshLoginUser(String nickName, String headImage, String userKey) {
		String tokenKey = getTokenKey(userKey);
		LoginUser loginUser = redisService.getCacheObject(tokenKey, LoginUser.class);
		loginUser.setNickName(nickName);
		loginUser.setHeadImage(headImage);
		redisService.setCacheObject(tokenKey, loginUser);
	}
	/**
	 * 从缓存获取登录用户
	 * @author IhavBB
	 * @date 23:37 2025/2/20
	 * @param token
	 * @param secret
	 * @return {@link LoginUser}
	**/
	public LoginUser getLoginUser(String token, String secret) {
		String userKey = getUserKey(token, secret);
		if (userKey == null) {
			return null;
		}
		return redisService.getCacheObject(getTokenKey(userKey), LoginUser.class);
	}

	/**
	 * 提取用户ID
	 * @author IhavBB
	 * @date 23:50 2025/2/20
	 * @param claims
	 * @return {@link Long}
	**/
	public Long getUserId(Claims claims) {
		if (claims == null) return null;
		return Long.valueOf(JwtUtils.getUserId(claims));  //获取jwt中的key
	}

	/**
	 * 提取UserKey
	 * @author IhavBB
	 * @date 23:48 2025/2/20
	 * @param token
	 * @param secret
	 * @return {@link String}
	**/
	private String getUserKey(String token, String secret) {
		Claims claims = getClaims(token, secret);
		if (claims == null) return null;
		//从claim中提取key
		return JwtUtils.getUserKey(claims);
	}


	public Claims getClaims(String token, String secret) {
		Claims claims;
		try {
			//获取令牌中信息  解析payload中信息  存储着用户唯一标识信息
			claims = JwtUtils.parseToken(token, secret);
			if (claims == null) {
				log.error("解析token：{}, 出现异常", token);
				return null;
			}
		} catch (Exception e) {
			log.error("解析token：{}, 出现异常", token, e);
			return null;
		}
		return claims;
	}

	/**
	 * 给userKey加上前缀用于去redis查信息
	 * @author IhavBB
	 * @date 23:48 2025/2/20
	 * @param userKey
	 * @return {@link String}
	**/
	private String getTokenKey(String userKey) {
		return CacheConstants.LOGIN_TOKEN_KEY + userKey;
	}

	/**
	 * 获取jwt载荷中的key
	 * @author IhavBB
	 * @date 23:55 2025/2/20
	 * @param claims
	 * @return {@link String}
	**/
	public String getUserKey(Claims claims) {
		if (claims == null) return null;
		return JwtUtils.getUserKey(claims);
	}

}
