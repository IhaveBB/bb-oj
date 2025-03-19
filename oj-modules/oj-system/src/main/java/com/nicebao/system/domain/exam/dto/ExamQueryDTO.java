package com.nicebao.system.domain.exam.dto;

import com.nicebao.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * ExamQueryDTO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/19
 */
@Getter
@Setter
public class ExamQueryDTO extends PageQueryDTO {

	private String title;

	private String startTime;

	private String endTime;
}
