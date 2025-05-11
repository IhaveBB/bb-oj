package com.nicebao.friend.controller.user;

import com.nicebao.common.core.constants.HttpConstants;
import com.nicebao.common.core.controller.BaseController;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.friend.aspect.CheckUserStatus;
import com.nicebao.friend.domain.exam.dto.ExamDTO;
import com.nicebao.friend.domain.exam.dto.ExamQueryDTO;
import com.nicebao.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * UserExamController
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/1
 */
@RestController
@RequestMapping("/user/exam")
public class UserExamController extends BaseController {

	@Autowired
	private IUserExamService userExamService;

	@CheckUserStatus
	@PostMapping("/enter")
	public R<Void> enter(@RequestHeader(HttpConstants.AUTHENTICATION) String token, @RequestBody ExamDTO examDTO) {
		return toR(userExamService.enter(token, examDTO.getExamId()));
	}

	@GetMapping("/list")
	public TableDataInfo list(ExamQueryDTO examQueryDTO) {
		return userExamService.list(examQueryDTO);
	}
}
