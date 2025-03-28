package com.nicebao.common.core.enums;

/**
 * UserStatus
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/28
 */
public enum UserStatus {

	Block(0),

	Normal(1),
	;


	private Integer value;

	UserStatus(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}
}
