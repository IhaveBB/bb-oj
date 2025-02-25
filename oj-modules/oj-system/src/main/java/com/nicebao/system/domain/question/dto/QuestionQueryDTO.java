package com.nicebao.system.domain.question.dto;

import com.nicebao.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * QuestionQueryDTO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/24
 */

@Getter
@Setter
public class QuestionQueryDTO extends PageQueryDTO {
	private Integer difficulty;

	private String title;

	private String excludeIdStr;

	private Set<Long> excludeIdSet;
}
