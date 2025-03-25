package com.nicebao.system.Service.user;

import com.nicebao.system.domain.user.dto.UserDTO;
import com.nicebao.system.domain.user.dto.UserQueryDTO;
import com.nicebao.system.domain.user.vo.UserVO;

import java.util.List;

/**
 * UserService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/25
 */
public interface UserService {
	List<UserVO> list(UserQueryDTO userQueryDTO);

	int updateStatus(UserDTO userDTO);

}
