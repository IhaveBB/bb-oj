package com.nicebao.system.controller.sysuser;

import com.nicebao.common.core.constants.HttpConstants;
import com.nicebao.common.core.controller.BaseController;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.domain.vo.LoginUserVO;
import com.nicebao.system.Service.sysuser.SysUserService;
import com.nicebao.system.Service.sysuser.impl.SysUserServiceImpl;
import com.nicebao.system.domain.sysuser.dto.LoginDTO;
import com.nicebao.system.domain.sysuser.dto.SysUserSaveDTO;
import com.nicebao.system.utils.BCryptUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * SystemUserController
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */

@RestController
@RequestMapping("/sysUser")
@Slf4j
@Tag(name = "管理员接口")
public class SysUserController extends BaseController {

	@Autowired
	private SysUserService sysUserService;

	@PostMapping("/login")  //安全
	@Operation(summary = "管理员登录", description = "根据账号密码进行管理员登录")
	@ApiResponse(responseCode = "1000", description = "操作成功")
	@ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
	@ApiResponse(responseCode = "3102", description = "用户不存在")
	@ApiResponse(responseCode = "3103", description = "用户名或密码错误")
	public R<String> login(@RequestBody LoginDTO loginDTO) {
		log.info("管理员登录:获取到账号ID{}",loginDTO.getUserAccount());
		return sysUserService.login(loginDTO.getUserAccount(), loginDTO.getPassword());
	}

	/**
	 * 注销
	 * @author IhavBB
	 * @date 23:36 2025/2/21
	 * @param token
	 * @return {@link R< Void>}
	**/
	@DeleteMapping("/logout")
	public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
		return toR(sysUserService.logout(token));
	}


	@GetMapping("/info")
	public R<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
		return sysUserService.info(token);
	}

	@PostMapping("/add")
	@Operation(summary = "新增管理员", description = "根据提供的信息新增管理员")
	@ApiResponse(responseCode = "1000", description = "操作成功")
	@ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
	@ApiResponse(responseCode = "3101", description = "用户已存在")
	public R<Void> add(@RequestBody SysUserSaveDTO sysUserSaveDTO) {
		return toR(sysUserService.add(sysUserSaveDTO));
	}


	public static void main(String[] args) {
		System.out.println(BCryptUtils.encryptPassword("123456"));
	}



}
