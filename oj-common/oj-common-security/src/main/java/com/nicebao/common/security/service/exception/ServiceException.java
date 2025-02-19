package com.nicebao.common.security.service.exception;

import com.nicebao.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * ServiceException
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
@Getter
public class ServiceException extends RuntimeException {

	private ResultCode resultCode;

	public ServiceException(ResultCode resultCode) {
		this.resultCode = resultCode;
	}
}
