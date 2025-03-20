package com.nicebao.system.controller.exam;

import com.nicebao.common.core.controller.BaseController;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.system.Service.exam.IExamService;
import com.nicebao.system.domain.exam.dto.ExamAddDTO;
import com.nicebao.system.domain.exam.dto.ExamQueryDTO;
import com.nicebao.system.domain.exam.dto.ExamQuestAddDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ExamController
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/19
 */

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {
	@Autowired
	private IExamService examService;

	@GetMapping("/list")
	public TableDataInfo list(ExamQueryDTO examQueryDTO) {
		return getTableDataInfo(examService.list(examQueryDTO));
	}

	@PostMapping("/add")
	public R<String> add(@RequestBody ExamAddDTO examAddDTO) {
		return R.ok(examService.add(examAddDTO));
	}

	@PostMapping("/question/add")
	public R<Void> questionAdd(@RequestBody ExamQuestAddDTO examQuestAddDTO) {
		return toR(examService.questionAdd(examQuestAddDTO));
	}

	@DeleteMapping("/question/delete")
	public R<Void> questionDelete(Long examId, Long questionId) {
		return toR(examService.questionDelete(examId, questionId));
	}



}
