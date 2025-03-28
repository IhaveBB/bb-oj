package com.nicebao.friend.controller.user;

import com.nicebao.common.core.constants.HttpConstants;
import com.nicebao.common.core.controller.BaseController;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.domain.vo.LoginUserVO;
import com.nicebao.friend.domain.user.dto.UserDTO;
import com.nicebao.friend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * UserController
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/25
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
	@Autowired
	private IUserService userService;

	@PostMapping("sendCode")
	public R<Void> sendCode(@RequestBody UserDTO userDTO) {
		return toR(userService.sendCode(userDTO)) ;
	}
	@PostMapping("/code/login")
	public R<String> codeLogin(@RequestBody UserDTO userDTO) {
		return R.ok(userService.codeLogin(userDTO.getPhone(), userDTO.getCode()));
	}
	@DeleteMapping("/logout")
	public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
		return toR(userService.logout(token));
	}
	@GetMapping("/info")
	public R<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
		return userService.info(token);
	}

}
