package com.nicebao.friend.service.user.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nicebao.common.core.constants.CacheConstants;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.constants.HttpConstants;
import com.nicebao.common.core.domain.LoginUser;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.domain.vo.LoginUserVO;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.core.enums.UserIdentity;
import com.nicebao.common.core.enums.UserStatus;
import com.nicebao.common.message.service.AliSmsService;
import com.nicebao.common.redis.service.RedisService;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.common.security.service.TokenService;
import com.nicebao.friend.domain.user.User;
import com.nicebao.friend.domain.user.dto.UserDTO;
import com.nicebao.friend.mapper.user.UserMapper;
import com.nicebao.friend.service.user.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UserServiceImpl
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/25
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {
	@Autowired
	private RedisService redisService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private TokenService tokenService;
	@Value("${jwt.secret}")
	private String secret;
	//验证码过期时间
	@Value("${sms.code-expiration:5}")
	private Long phoneCodeExpiration;
	//验证码每天发送的最大次数
	@Value("${sms.send-limit:3}")
	private Integer sendLimit;
	//是否发送验证码开关
	@Value("${sms.is-send:false}")
	private boolean isSend;
	@Autowired
	private AliSmsService aliSmsService;
	@Value("${file.oss.downloadUrl}")
	private String downloadUrl;

	@Override
	public boolean sendCode(UserDTO userDTO) {
		if (!checkPhone(userDTO.getPhone())) {
			throw new ServiceException(ResultCode.FAILED_USER_PHONE);
		}
		String phoneCodeKey =  getPhoneCodeKey(userDTO.getPhone());
		//看看过期时间
		Long expire = redisService.getExpire(phoneCodeKey, TimeUnit.SECONDS);
		if (expire != null && (phoneCodeExpiration * 60 - expire) < 60 ){
			throw new ServiceException(ResultCode.FAILED_FREQUENT);
		}
		//每天的验证码获取次数有一个限制  50次  第二天  计数清0 重新开始计数
		//操作这个次数数据频繁   、 不需要存储、  记录的次数 有有效时间的（当天有效） redis  String  key：c:t:手机号
		//获取已经请求的次数  和50 进行比较     如果大于限制抛出异常。如果不大于限制，正常执行后续逻辑，并且将获取计数 + 1
		String codeTimeKey = getCodeTimeKey(userDTO.getPhone());
		Long sendTimes = redisService.getCacheObject(codeTimeKey, Long.class);
		if (sendTimes != null && sendTimes >= sendLimit) {
			throw new ServiceException(ResultCode.FAILED_TIME_LIMIT);
		}
		//如果不开启发送验证码，就使用默认验证码123456
		String code = isSend ? RandomUtil.randomNumbers(6) : Constants.DEFAULT_CODE;
		//存储到redis  数据结构：String  key：p:c:手机号  value :code
		redisService.setCacheObject(phoneCodeKey, code, phoneCodeExpiration, TimeUnit.MINUTES);
		if (isSend) {
			boolean sendMobileCode = aliSmsService.sendMobileCode(userDTO.getPhone(), code);
			if (!sendMobileCode) {
				throw new ServiceException(ResultCode.FAILED_SEND_CODE);
			}
		}
		redisService.increment(codeTimeKey);
		//说明是当天第一次发起获取验证码的请求
		if (sendTimes == null) {
			long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
					LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
			redisService.expire(codeTimeKey, seconds, TimeUnit.SECONDS);
		}
		return true;
	}

	@Override
	public String codeLogin(String phone, String code) {
		checkCode(phone, code);
		User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
		//新用户
		if (user == null) {
			//注册逻辑
			user = new User();
			user.setPhone(phone);
			user.setStatus(UserStatus.Normal.getValue());
			user.setCreateBy(Constants.SYSTEM_USER_ID);
			userMapper.insert(user);
		}
		return tokenService.createToken(user.getUserId(), secret, UserIdentity.ORDINARY.getValue(), user.getNickName(), user.getHeadImage());
	}

	@Override
	public boolean logout(String token) {
		if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
			token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
		}
		return tokenService.deleteLoginUser(token, secret);
	}

	@Override
	public R<LoginUserVO> info(String token) {
		if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
			token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
		}
		LoginUser loginUser = tokenService.getLoginUser(token, secret);
		if (loginUser == null) {
			return R.fail();
		}
		LoginUserVO loginUserVO = new LoginUserVO();
		loginUserVO.setNickName(loginUser.getNickName());
		if (StrUtil.isNotEmpty(loginUser.getHeadImage())) {
			loginUserVO.setHeadImage(downloadUrl + loginUser.getHeadImage());
		}
		return R.ok(loginUserVO);
	}


	private void checkCode(String phone, String code) {
		String phoneCodeKey = getPhoneCodeKey(phone);
		String cacheCode = redisService.getCacheObject(phoneCodeKey, String.class);
		if (StrUtil.isEmpty(cacheCode)) {
			throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
		}
		if (!cacheCode.equals(code)) {
			throw new ServiceException(ResultCode.FAILED_ERROR_CODE);
		}
		//验证码比对成功
		redisService.deleteObject(phoneCodeKey);
	}

	private String getCodeTimeKey(String phone) {
		return CacheConstants.CODE_TIME_KEY + phone;
	}

	public static boolean checkPhone(String phone) {
		Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
		Matcher m = regex.matcher(phone);
		return m.matches();
	}

	private String getPhoneCodeKey(String phone) {
		return CacheConstants.PHONE_CODE_KEY + phone;
	}
}
