package com.nicebao.friend.domain.question.dto;

import com.nicebao.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * QuestionQueryDTO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/12
 */
@Getter
@Setter
public class QuestionQueryDTO extends PageQueryDTO {

	private String keyword;

	private Integer difficulty;
}
