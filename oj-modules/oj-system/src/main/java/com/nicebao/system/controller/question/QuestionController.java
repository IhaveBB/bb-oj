package com.nicebao.system.controller.question;

import com.nicebao.common.core.controller.BaseController;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.system.Service.question.IQuestionService;
import com.nicebao.system.domain.question.dto.QuestionAddDTO;
import com.nicebao.system.domain.question.dto.QuestionEditDTO;
import com.nicebao.system.domain.question.dto.QuestionQueryDTO;
import com.nicebao.system.domain.question.vo.QuestionDetailVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping("/add")
	public R<Void> add(@RequestBody QuestionAddDTO questionAddDTO) {
		return toR(questionService.add(questionAddDTO));
	}

	@GetMapping("/detail")
	public R<QuestionDetailVO> detail(Long questionId) {
		return R.ok(questionService.detail(questionId));
	}

	//  /question/edit
	@PutMapping("/edit")
	public R<Void> edit(@RequestBody QuestionEditDTO questionEditDTO) {
		return toR(questionService.edit(questionEditDTO));
	}

	//  /question/delete
	@DeleteMapping("/delete")
	public R<Void> delete(Long questionId) {
		return toR(questionService.delete(questionId));
	}
}
