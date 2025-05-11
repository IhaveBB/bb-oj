package com.nicebao.friend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * UserSubmitDTO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/13
 */
@Getter
@Setter
public class UserSubmitDTO {

	private Long examId;  //可选

	private Long questionId;
	// (0: java  1:cpp 2: golang)
	private Integer programType;

	private String userCode;
}
