package com.nicebao.system.mapper.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nicebao.system.domain.exam.ExamQuestion;
import com.nicebao.system.domain.question.vo.QuestionVO;

import java.util.List;

/**
 * ExamQuestionMapper
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/20
 */
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {

	List<QuestionVO> selectExamQuestionList(Long examId);
}

