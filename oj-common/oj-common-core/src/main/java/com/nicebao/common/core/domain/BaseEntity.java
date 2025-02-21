package com.nicebao.common.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BaseEntity
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
@Getter
@Setter
public class BaseEntity {

	@TableField(fill = FieldFill.INSERT)
	private Long createBy;

	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@TableField(fill = FieldFill.UPDATE)
	private Long updateBy;

	@TableField(fill = FieldFill.UPDATE)
	private LocalDateTime updateTime;
}