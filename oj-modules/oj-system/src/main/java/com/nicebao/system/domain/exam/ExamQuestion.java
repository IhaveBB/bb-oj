package com.nicebao.system.domain.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nicebao.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * ExamQuestion
 * <p>
 * \
 *
 * @author IhaveBB
 * @date 2025/3/20
 */
@Getter
@Setter
@TableName("tb_exam_question")
public class ExamQuestion extends BaseEntity {

	@TableId(value = "EXAM_QUESTION_ID", type = IdType.ASSIGN_ID)
	private Long examQuestionId;

	private Long examId;

	private Long questionId;

	private Integer questionOrder;
}
