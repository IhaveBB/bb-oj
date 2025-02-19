package com.nicebao.system.Service.sysuser;

import com.nicebao.common.core.domain.R;

/**
 * SysUserService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
public interface SysUserService {
	R<String> login(String userAccount, String password);

}
