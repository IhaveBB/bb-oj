package com.nicebao.system.Service.question;

import com.nicebao.system.domain.question.dto.QuestionAddDTO;
import com.nicebao.system.domain.question.dto.QuestionQueryDTO;
import com.nicebao.system.domain.question.vo.QuestionVO;

import java.util.List;

/**
 * IQuestionService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/24
 */
public interface IQuestionService {

	List<QuestionVO> list(QuestionQueryDTO questionQueryDTO);

	/**
	 * 添加题目
	 * @author IhavBB
	 * @date 20:09 2025/2/25
	 * @param questionAddDTO
	 * @return {@link boolean}
	**/
	boolean add(QuestionAddDTO questionAddDTO);

}
