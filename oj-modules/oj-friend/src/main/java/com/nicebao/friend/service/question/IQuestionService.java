package com.nicebao.friend.service.question;

import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.friend.domain.question.dto.QuestionQueryDTO;
import com.nicebao.friend.domain.question.vo.QuestionDetailVO;
import com.nicebao.friend.domain.question.vo.QuestionVO;

import java.util.List;

/**
 * IQuestionService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/5
 */
public interface IQuestionService {

	TableDataInfo list(QuestionQueryDTO questionQueryDTO);

	List<QuestionVO> hotList();

	QuestionDetailVO detail(Long questionId);

	String preQuestion(Long questionId);

	String nextQuestion(Long questionId);
}
