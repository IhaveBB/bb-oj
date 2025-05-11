package com.nicebao.friend.service.user;
import com.nicebao.api.domain.vo.UserQuestionResultVO;
import com.nicebao.common.core.domain.R;
import com.nicebao.friend.domain.user.dto.UserSubmitDTO;

/**
 * IUserQuestionService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/4
 */
public interface IUserQuestionService {
	R<UserQuestionResultVO> submit(UserSubmitDTO submitDTO);

	boolean rabbitSubmit(UserSubmitDTO submitDTO);

	UserQuestionResultVO exeResult(Long examId, Long questionId, String currentTime);
}
