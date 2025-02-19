package com.nicebao.common.core.controller;

import com.nicebao.common.core.domain.R;

/**
 * BaseController
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
public class BaseController {
	public R<Void> toR(int rows) {
		return rows > 0 ? R.ok() : R.fail();
	}

	public R<Void> toR(boolean result) {
		return result ? R.ok() : R.fail();
	}
}
