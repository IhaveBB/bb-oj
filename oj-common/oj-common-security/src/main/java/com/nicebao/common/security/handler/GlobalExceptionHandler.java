package com.nicebao.common.security.handler;

import com.nicebao.common.core.domain.R;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.security.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHander
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	/**
	 * 请求方式不支持
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public R<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
													HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
		return R.fail(ResultCode.ERROR);
	}

	@ExceptionHandler(ServiceException.class)
	public R<?> handleServiceException(ServiceException e, HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		ResultCode resultCode = e.getResultCode();
		log.error("请求地址'{}',发生业务异常: {}", requestURI, resultCode.getMsg(), e);
		return R.fail(resultCode);
	}

//	@ExceptionHandler(BindException.class)
//	public R<Void> handleBindException(BindException e) {
//		log.error(e.getMessage());
//		String message = join(e.getAllErrors(),
//				DefaultMessageSourceResolvable::getDefaultMessage, ", ");
//		return R.fail(ResultCode.FAILED_PARAMS_VALIDATE.getCode(), message);
//	}

//	private <E> String join(Collection<E> collection, Function<E, String>
//			function, CharSequence delimiter) {
//		if (CollUtil.isEmpty(collection)) {
//			return StrUtil.EMPTY;
//		}
//		return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
//	}

	/**
	 * 拦截运行时异常
	 */
	@ExceptionHandler(RuntimeException.class)
	public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		log.error("请求地址'{}',发生运行时异常.", requestURI, e);
		return R.fail(ResultCode.ERROR);
	}

	/**
	 * 系统异常
	 */
	@ExceptionHandler(Exception.class)
	public R<?> handleException(Exception e, HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		log.error("请求地址'{}',发生异常.", requestURI, e);
		return R.fail(ResultCode.ERROR);
	}
}
