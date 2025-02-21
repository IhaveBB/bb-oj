package com.nicebao.system.Service.sysuser.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.core.enums.UserIdentity;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.common.security.service.TokenService;
import com.nicebao.system.Service.sysuser.SysUserService;
import com.nicebao.system.domain.SysUser;
import com.nicebao.system.domain.sysuser.dto.SysUserSaveDTO;
import com.nicebao.system.mapper.sysuser.SysUserMapper;
import com.nicebao.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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

	@Override
	public int add(SysUserSaveDTO sysUserSaveDTO) {
		List<SysUser> sysUserList = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
				.eq(SysUser::getUserAccount, sysUserSaveDTO.getUserAccount()));
		//用户信息已存在不可以重复注册
		if (CollectionUtil.isNotEmpty(sysUserList)) {
			throw new ServiceException(ResultCode.AILED_USER_EXISTS);
		}
		SysUser sysUser = new SysUser();
		sysUser.setUserAccount(sysUserSaveDTO.getUserAccount());
		sysUser.setPassword(BCryptUtils.encryptPassword(sysUserSaveDTO.getPassword()));
		sysUser.setCreateBy(Constants.SYSTEM_USER_ID);
		return sysUserMapper.insert(sysUser);
	}

	public static void main(String[] args) {
		SysUser sysUser = new SysUser();
		sysUser.setUserAccount("IhaveBB");
		sysUser.setPassword(BCryptUtils.encryptPassword("www.92921801"));
		sysUser.setCreateBy(Constants.SYSTEM_USER_ID);
		System.out.println(sysUser.toString());
	}
}

