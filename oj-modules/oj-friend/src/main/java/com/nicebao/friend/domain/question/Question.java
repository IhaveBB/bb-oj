package com.nicebao.friend.domain.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nicebao.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Question
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/12
 */

@TableName("tb_question")
@Getter
@Setter
public class Question extends BaseEntity {

	@TableId(type = IdType.ASSIGN_ID)
	private Long questionId;

	private String title;

	private Integer difficulty;

	private Long timeLimit;

	private Long spaceLimit;

	private String content;

	private String questionCase;

	private String defaultCode;

	private String mainFuc;
}
