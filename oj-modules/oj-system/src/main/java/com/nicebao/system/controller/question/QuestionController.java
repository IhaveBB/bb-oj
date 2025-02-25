package com.nicebao.system.controller.question;

import com.nicebao.common.core.controller.BaseController;
import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.system.Service.question.IQuestionService;
import com.nicebao.system.domain.question.dto.QuestionQueryDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * QuestionController
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/24
 */

@RestController
@RequestMapping("/question")
@Tag(name = "题目管理接口")
public class QuestionController extends BaseController {
	@Autowired
	private IQuestionService questionService;

	@GetMapping("/list")
	public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
		return getTableDataInfo(questionService.list(questionQueryDTO));
	}

}
