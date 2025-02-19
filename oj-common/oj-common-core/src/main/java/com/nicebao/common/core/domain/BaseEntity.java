package com.nicebao.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BaseEntity
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
@Data
public class BaseEntity implements Serializable {
	// 创建用户
	private Long createBy;

	// 创建时间
	private LocalDateTime createTime;

	// 更新用户
	private Long updateBy;

	// 更新时间
	private LocalDateTime updateTime;
}
