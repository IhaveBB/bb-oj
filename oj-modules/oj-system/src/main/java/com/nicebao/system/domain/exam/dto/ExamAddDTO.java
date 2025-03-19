package com.nicebao.system.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ExamAddDTO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/19
 */

@Getter
@Setter
public class ExamAddDTO {

	private String title;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;
}
