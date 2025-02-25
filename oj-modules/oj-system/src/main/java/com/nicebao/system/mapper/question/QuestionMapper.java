package com.nicebao.system.mapper.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nicebao.system.domain.question.Question;
import com.nicebao.system.domain.question.dto.QuestionQueryDTO;
import com.nicebao.system.domain.question.vo.QuestionVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * QuestionMapper
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/25
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

	List<QuestionVO> selectQuestionList(QuestionQueryDTO questionQueryDTO);
}
