package com.nicebao.system.controller;

import com.nicebao.common.core.domain.R;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SystemUserController
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
@RestController
public class SystemUserController {
	public R<String> login(@RequestBody LoginDTO loginDTO) {
		return sysUserService.login(loginDTO.getUserAccount(), loginDTO.getPassword());
	}
}
