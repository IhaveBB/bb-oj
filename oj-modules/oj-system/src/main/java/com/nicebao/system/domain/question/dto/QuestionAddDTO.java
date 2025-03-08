package com.nicebao.system.domain.question.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * QuestionAddDTO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/25
 */
@Getter
@Setter
public class QuestionAddDTO {

	private String title;

	private Integer difficulty;

	private Long timeLimit;

	private Long spaceLimit;

	private String content;

	private String questionCase;

	private String defaultCode;

	private String mainFuc;
}
