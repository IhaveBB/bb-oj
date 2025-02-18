package com.nicebao.system.domain;

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
	private Integer testId;
	private String title;
	private String content;
}
