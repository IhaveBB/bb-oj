package com.nicebao.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * TestDomain
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/18
 */
@TableName("tb_test")
@Data
public class TestDomain {
	@TableId(type = IdType.ASSIGN_ID)
	private String testId;
	private String title;
	private String content;
}
