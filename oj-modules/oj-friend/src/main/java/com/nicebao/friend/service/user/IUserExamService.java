package com.nicebao.friend.service.user;

import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.friend.domain.exam.dto.ExamQueryDTO;

/**
 * IUserExamService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/1
 */
public interface IUserExamService {

	int enter(String token, Long examId);

	TableDataInfo list(ExamQueryDTO examQueryDTO);
}