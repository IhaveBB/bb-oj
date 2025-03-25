package com.nicebao.system.controller.user;

import com.nicebao.common.core.controller.BaseController;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.system.Service.user.UserService;
import com.nicebao.system.domain.user.dto.UserDTO;
import com.nicebao.system.domain.user.dto.UserQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * UserController
 * <p>
 * 管理C端用户
 * @author IhaveBB
 * @date 2025/3/25
 */
@Controller
public class UserController extends BaseController {

	@Autowired
	private UserService userService;

	/**
	 * 获取C端用户列表
	 * @author IhavBB
	 * @date 20:09 2025/3/25
	 * @param userQueryDTO
	 * @return {@link TableDataInfo}
	**/
	@GetMapping("/list")
	public TableDataInfo list(UserQueryDTO userQueryDTO) {
		//todo 这里是为什么
		return getTableDataInfo(userService.list(userQueryDTO));
	}


	/**
	 * 更新数据库中用户的状态信息。
	 * @author IhavBB
	 * @date 20:08 2025/3/25
	 * @param userDTO
	 * @return {@link R< Void>}
	**/
	@PutMapping("/updateStatus")
	//todo 拉黑：限制用户操作   解禁：放开对于用户限制
	public R<Void> updateStatus(@RequestBody UserDTO userDTO) {
		return toR(userService.updateStatus(userDTO));
	}

}
