package com.nicebao.system.Service.sysuser;

import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.domain.vo.LoginUserVO;
import com.nicebao.system.domain.sysuser.dto.SysUserSaveDTO;

/**
 * SysUserService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
public interface SysUserService {
	R<String> login(String userAccount, String password);

	int add(SysUserSaveDTO sysUserSaveDTO);

	boolean logout(String token);

	R<LoginUserVO> info(String token);
}
