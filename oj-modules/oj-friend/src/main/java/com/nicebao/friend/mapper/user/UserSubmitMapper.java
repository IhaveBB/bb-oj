package com.nicebao.friend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nicebao.friend.domain.user.UserSubmit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * UserSubmitMapper
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/12
 */
public interface UserSubmitMapper extends BaseMapper<UserSubmit> {

	UserSubmit selectCurrentUserSubmit(Long userId, Long examId, Long questionId, String currentTime);

	List<Long> selectHostQuestionList();
}
