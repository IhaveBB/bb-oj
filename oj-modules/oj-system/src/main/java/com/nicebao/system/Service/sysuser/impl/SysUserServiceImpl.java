package com.nicebao.system.Service.sysuser.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.core.enums.UserIdentity;
import com.nicebao.common.security.service.service.TokenService;
import com.nicebao.system.Service.sysuser.SysUserService;
import com.nicebao.system.domain.SysUser;
import com.nicebao.system.mapper.sysuser.SysUserMapper;
import com.nicebao.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SysUserServiceImpl
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
@Service
public class SysUserServiceImpl implements SysUserService {
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private TokenService tokenService;

	@Value("${jwt.secret}")
	private String secret;

	@Override
	public R<String> login(String userAccount, String password) {
		//通过账号去数据库中查询，对应的用户信息
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		SysUser sysUser = sysUserMapper.selectOne(queryWrapper
				.select(SysUser::getUserId, SysUser::getPassword, SysUser::getNickName)
				.eq(SysUser::getUserAccount, userAccount));
		if (sysUser == null) {
			return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
		}
		if (BCryptUtils.matchesPassword(password, sysUser.getPassword())) {
			return R.ok(tokenService.createToken(sysUser.getUserId(),
					secret, UserIdentity.ADMIN.getValue(), sysUser.getNickName(), null));
		}
		return R.fail(ResultCode.FAILED_LOGIN);
	}
}

