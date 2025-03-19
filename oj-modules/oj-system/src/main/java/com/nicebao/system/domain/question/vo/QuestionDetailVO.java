package com.nicebao.system.domain.question.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

/**
 * QuestionDetailVO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/19
 */
@Getter
@Setter
public class QuestionDetailVO {

	@JsonSerialize(using = ToStringSerializer.class)
	private Long questionId;

	private String title;

	private Integer difficulty;

	private Long timeLimit;

	private Long spaceLimit;

	private String content;

	private String questionCase;

	private String defaultCode;

	private String mainFuc;
}

