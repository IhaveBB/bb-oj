package com.nicebao.friend.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nicebao.common.core.constants.CacheConstants;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.redis.service.RedisService;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.friend.domain.question.Question;
import com.nicebao.friend.mapper.question.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * QuestionCacheManager
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/12
 */
@Component
public class QuestionCacheManager {

	@Autowired
	private RedisService redisService;

	@Autowired
	private QuestionMapper questionMapper;

	/**
	 * 获取缓存中问题列表个数
	 * @author IhavBB
	 * @date 17:21 2025/4/12
	 * @return {@link Long}
	**/
	public Long getListSize() {
		return redisService.getListSize(CacheConstants.QUESTION_LIST);
	}
	/**
	 * 获取缓存中热门题目个数
	 * @author IhavBB
	 * @date 17:21 2025/4/12
	 * @return {@link Long}
	**/
	public Long getHostListSize() {
		return redisService.getListSize(CacheConstants.QUESTION_HOST_LIST);
	}
	/**
	 * 刷新缓存
	 * @author IhavBB
	 * @date 17:22 2025/4/12
	**/
	public void refreshCache() {
		List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
				.select(Question::getQuestionId).orderByDesc(Question::getCreateTime));
		if (CollectionUtil.isEmpty(questionList)) {
			return;
		}
		List<Long> questionIdList = questionList.stream().map(Question::getQuestionId).toList();
		redisService.rightPushAll(CacheConstants.QUESTION_LIST, questionIdList);
	}
	/**
	 * 从缓存获取前一个题目ID
	 * @author IhavBB
	 * @date 17:22 2025/4/12
	 * @param questionId 当前题目ID
	 * @return {@link Long}
	**/
	public Long preQuestion(Long questionId) {
		Long index = redisService.indexOfForList(CacheConstants.QUESTION_LIST, questionId);
		if (index == 0) {
			throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
		}
		return redisService.indexForList(CacheConstants.QUESTION_LIST, index - 1, Long.class);
	}
	/**
	 * 从缓存中获取下一个题目ID
	 * @author IhavBB
	 * @date 17:23 2025/4/12
	 * @param questionId 当前题目ID
	 * @return {@link Object}
	**/
	public Object nextQuestion(Long questionId) {
		Long index = redisService.indexOfForList(CacheConstants.QUESTION_LIST, questionId);
		long lastIndex = getListSize() - 1;
		if (index == lastIndex) {
			throw new ServiceException(ResultCode.FAILED_LAST_QUESTION);
		}
		return redisService.indexForList(CacheConstants.QUESTION_LIST, index + 1, Long.class);
	}
	/**
	 * 从缓存获取热门题目列表
	 * @author IhavBB
	 * @date 17:27 2025/4/12
	 * @return {@link List< Long>}
	**/
	public List<Long> getHostList() {
		return redisService.getCacheListByRange(CacheConstants.QUESTION_HOST_LIST,
				CacheConstants.DEFAULT_START, CacheConstants.DEFAULT_END, Long.class);
	}
	/**
	 * 插入刷洗热门题目列表缓存
	 * @author IhavBB
	 * @date 17:28 2025/4/12
	 * @param hotQuestionIdList 热门题目列表
	**/
	public void refreshHotQuestionList(List<Long> hotQuestionIdList) {
		if (CollectionUtil.isEmpty(hotQuestionIdList)) {
			return;
		}
		redisService.rightPushAll(CacheConstants.QUESTION_HOST_LIST, hotQuestionIdList);
	}
}
