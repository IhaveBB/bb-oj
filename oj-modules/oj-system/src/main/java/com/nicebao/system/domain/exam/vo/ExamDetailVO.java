package com.nicebao.system.domain.exam.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nicebao.system.domain.question.vo.QuestionVO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ExamDetailVO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/20
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamDetailVO {

	private String title;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

	private List<QuestionVO> examQuestionList;
}