package com.nicebao.system.domain.exam.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;

/**
 * ExamQuestAddDTO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/20
 */
@Getter
@Setter
public class ExamQuestAddDTO {
	private Long examId;
	private LinkedHashSet<Long> questionIdSet;
}

