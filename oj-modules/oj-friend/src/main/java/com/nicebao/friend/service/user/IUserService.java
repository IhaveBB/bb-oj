package com.nicebao.friend.service.user;

import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.domain.vo.LoginUserVO;
import com.nicebao.friend.domain.user.dto.UserDTO;

/**
 * IUserService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/25
 */
public interface IUserService {
	boolean sendCode(UserDTO userDTO);
	String codeLogin(String phone, String code);

	boolean logout(String token);

	R<LoginUserVO> info(String token);

}
