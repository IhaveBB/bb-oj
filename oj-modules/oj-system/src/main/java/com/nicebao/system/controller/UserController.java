//package com.nicebao.system.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
///**
// * UserController
// * <p>
// *
// * @author IhaveBB
// * @date 2025/2/19
// */
//@RestController
//@RequestMapping("/user")
//public class UserController {
//	@Autowired
//	private IUserService userService;
//	@GetMapping("/list")
//	public TableDataInfo list(UserQueryDTO userQueryDTO) {
//		return getTableDataInfo(userService.list(userQueryDTO));
//	}
//
//	@PutMapping("/updateStatus")
//	//todo 拉黑：限制用户操作   解禁：放开对于用户限制
//	//更新数据库中用户的状态信息。
//	public R<Void> updateStatus(@RequestBody UserDTO userDTO) {
//		return toR(userService.updateStatus(userDTO));
//	}
//}
