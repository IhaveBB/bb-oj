package com.nicebao.system.Service.user.impl;

import com.github.pagehelper.PageHelper;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.system.Service.user.UserService;
import com.nicebao.system.domain.user.User;
import com.nicebao.system.domain.user.dto.UserDTO;
import com.nicebao.system.domain.user.dto.UserQueryDTO;
import com.nicebao.system.domain.user.vo.UserVO;
import com.nicebao.system.mapper.user.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/25
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
	@Autowired
	private UserMapper userMapper;

	@Override
	public List<UserVO> list(UserQueryDTO userQueryDTO) {
		PageHelper.startPage(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
		return userMapper.selectUserList(userQueryDTO);
	}

	@Override
	public int updateStatus(UserDTO userDTO) {
		User user = userMapper.selectById(userDTO.getUserId());
		if (user == null) {
			throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
		}
		user.setStatus(userDTO.getStatus());
		//todo 这里后期去缓存更新信息
		return userMapper.updateById(user);
	}
}
