package com.nicebao.friend.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.nicebao.common.core.constants.CacheConstants;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.enums.ExamListType;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.redis.service.RedisService;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.friend.domain.user.UserExam;
import com.nicebao.friend.domain.exam.Exam;
import com.nicebao.friend.domain.exam.ExamQuestion;
import com.nicebao.friend.domain.exam.dto.ExamQueryDTO;
import com.nicebao.friend.domain.exam.dto.ExamRankDTO;
import com.nicebao.friend.domain.exam.vo.ExamRankVO;
import com.nicebao.friend.domain.exam.vo.ExamVO;
import com.nicebao.friend.mapper.exam.ExamMapper;
import com.nicebao.friend.mapper.exam.ExamQuestionMapper;
import com.nicebao.friend.mapper.user.UserExamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ExamCacheManager {

	@Autowired
	private ExamMapper examMapper;

	@Autowired
	private ExamQuestionMapper examQuestionMapper;

	@Autowired
	private UserExamMapper userExamMapper;

	@Autowired
	private RedisService redisService;

	public Long getListSize(Integer examListType, Long userId) {
		String examListKey = getExamListKey(examListType, userId);
		return redisService.getListSize(examListKey);
	}

	public Long getExamQuestionListSize(Long examId) {
		String examQuestionListKey = getExamQuestionListKey(examId);
		return redisService.getListSize(examQuestionListKey);
	}

	public Long getRankListSize(Long examId) {
		return redisService.getListSize(getExamRankListKey(examId));
	}

	public List<ExamVO> getExamVOList(ExamQueryDTO examQueryDTO, Long userId) {
		int start = (examQueryDTO.getPageNum() - 1) * examQueryDTO.getPageSize();
		//下标需要 -1
		int end = start + examQueryDTO.getPageSize() - 1;
		String examListKey = getExamListKey(examQueryDTO.getType(), userId);
		List<Long> examIdList = redisService.getCacheListByRange(examListKey, start, end, Long.class);
		List<ExamVO> examVOList = assembleExamVOList(examIdList);
		if (CollectionUtil.isEmpty(examVOList)) {
			//说明redis中数据可能有问题 从数据库中查数据并且重新刷新缓存
			//从数据库中获取数据
			examVOList = getExamListByDB(examQueryDTO, userId);
			refreshCache(examQueryDTO.getType(), userId);
		}
		return examVOList;
	}
	/**
	 * 获取竞赛排行榜
	 * @author IhavBB
	 * @date 0:02 2025/3/29
	 * @param examRankDTO
	 * @return {@link List< ExamRankVO>}
	**/
	public List<ExamRankVO> getExamRankList(ExamRankDTO examRankDTO) {
		int start = (examRankDTO.getPageNum() - 1) * examRankDTO.getPageSize();
		int end = start + examRankDTO.getPageSize() - 1; //下标需要 -1
		return redisService.getCacheListByRange(getExamRankListKey(examRankDTO.getExamId()), start, end, ExamRankVO.class);
	}

	public List<Long> getAllUserExamList(Long userId) {
		String examListKey = CacheConstants.USER_EXAM_LIST + userId;
		List<Long> userExamIdList = redisService.getCacheListByRange(examListKey, 0, -1, Long.class);
		if (CollectionUtil.isNotEmpty(userExamIdList)) {
			return userExamIdList;
		}
		List<UserExam> userExamList =
				userExamMapper.selectList(new LambdaQueryWrapper<UserExam>().eq(UserExam::getUserId, userId));
		if (CollectionUtil.isEmpty(userExamList)) {
			return null;
		}
		refreshCache(ExamListType.USER_EXAM_LIST.getValue(), userId);
		return userExamList.stream().map(UserExam::getExamId).collect(Collectors.toList());
	}

	public void addUserExamCache(Long userId, Long examId) {
		String userExamListKey = getUserExamListKey(userId);
		redisService.leftPushForList(userExamListKey, examId);
	}

	public Long getFirstQuestion(Long examId) {
		return redisService.indexForList(getExamQuestionListKey(examId), 0, Long.class);
	}

	public Long preQuestion(Long examId, Long questionId) {
		Long index = redisService.indexOfForList(getExamQuestionListKey(examId), questionId);
		if (index == 0) {
			throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
		}
		return redisService.indexForList(getExamQuestionListKey(examId), index - 1, Long.class);
	}

	public Long nextQuestion(Long examId, Long questionId) {
		Long index = redisService.indexOfForList(getExamQuestionListKey(examId), questionId);
		long lastIndex = getExamQuestionListSize(examId) - 1;
		if (index == lastIndex) {
			throw new ServiceException(ResultCode.FAILED_LAST_QUESTION);
		}
		return redisService.indexForList(getExamQuestionListKey(examId), index + 1, Long.class);
	}

	/**
	 * 刷新缓存
	 * @author IhavBB
	 * @date 23:21 2025/3/28
	 * @param examListType 0:未完赛列表 1:历史列表 2:用户竞赛列表
	 * @param userId
	**/
	public void refreshCache(Integer examListType, Long userId) {
		List<Exam> examList = new ArrayList<>();
		if (ExamListType.EXAM_UN_FINISH_LIST.getValue().equals(examListType)) {
			//查询未完赛的竞赛列表
			examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
					.select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
					.gt(Exam::getEndTime, LocalDateTime.now())
					.eq(Exam::getStatus, Constants.TRUE)
					.orderByDesc(Exam::getCreateTime));
		} else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
			//查询历史竞赛
			examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
					.select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
					.le(Exam::getEndTime, LocalDateTime.now())
					.eq(Exam::getStatus, Constants.TRUE)
					.orderByDesc(Exam::getCreateTime));
		} else if (ExamListType.USER_EXAM_LIST.getValue().equals(examListType)) {
			List<ExamVO> examVOList = userExamMapper.selectUserExamList(userId);
			examList = BeanUtil.copyToList(examVOList, Exam.class);
		}
		if (CollectionUtil.isEmpty(examList)) {
			return;
		}

		Map<String, Exam> examMap = new HashMap<>();
		List<Long> examIdList = new ArrayList<>();
		for (Exam exam : examList) {
			examMap.put(getDetailKey(exam.getExamId()), exam);
			examIdList.add(exam.getExamId());
		}
		//刷新详情缓存
		redisService.multiSet(examMap);
		//删除列表缓存
		redisService.deleteObject(getExamListKey(examListType, userId));
		//刷新列表缓存
		redisService.rightPushAll(getExamListKey(examListType, userId), examIdList);
	}

	/**
	 * 刷新竞赛中的问题列表缓存
	 * @author IhavBB
	 * @date 8:21 2025/3/29
	 * @param examId 竞赛ID
	**/
	public void refreshExamQuestionCache(Long examId) {
		List<ExamQuestion> examQuestionList = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
				.select(ExamQuestion::getQuestionId)
				.eq(ExamQuestion::getExamId, examId)
				.orderByAsc(ExamQuestion::getQuestionOrder));
		if (CollectionUtil.isEmpty(examQuestionList)) {
			return;
		}
		List<Long> examQuestionIdList = examQuestionList.stream().map(ExamQuestion::getQuestionId).toList();
		redisService.rightPushAll(getExamQuestionListKey(examId), examQuestionIdList);
		//节省 redis缓存资源
		long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
				LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
		redisService.expire(getExamQuestionListKey(examId), seconds, TimeUnit.SECONDS);
	}

	/**
	 * 刷新竞赛排行榜缓存
	 * @author IhavBB
	 * @date 0:05 2025/3/29
	 * @param examId 竞赛ID
	**/
	public void refreshExamRankCache(Long examId) {
		List<ExamRankVO> examRankVOList = userExamMapper.selectExamRankList(examId);
		if (CollectionUtil.isEmpty(examRankVOList)) {
			return;
		}
		redisService.rightPushAll(getExamRankListKey(examId), examRankVOList);
	}

	/**
	 * 从DB获取竞赛列表
	 * <p>
	 * 根据ExamQueryDTO.getType来判断查询哪些竞赛
	 * @author IhavBB
	 * @date 23:19 2025/3/28
	 * @param examQueryDTO
	 * @param userId
	 * @return {@link List< ExamVO>}
	**/
	private List<ExamVO> getExamListByDB(ExamQueryDTO examQueryDTO, Long userId) {
		PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
		if (ExamListType.USER_EXAM_LIST.getValue().equals(examQueryDTO.getType())) {
			//查询我的竞赛列表
			return userExamMapper.selectUserExamList(userId);
		} else {
			//查询C端的竞赛列表
			return examMapper.selectExamList(examQueryDTO);
		}
	}

	/**
	 * 根据examIdList去Redis查询竞赛详情
	 * <p>
	 * 如果数据有异常返回null(如获取到的数据数量和传入的List的size大小不匹配)
	 * @author IhavBB
	 * @date 23:02 2025/3/28
	 * @param examIdList
	 * @return {@link List< ExamVO>}
	**/
	private List<ExamVO> assembleExamVOList(List<Long> examIdList) {
		if (CollectionUtil.isEmpty(examIdList)) {
			//说明redis当中没数据 从数据库中查数据并且重新刷新缓存
			return null;
		}
		//拼接redis当中key的方法 并且将拼接好的key存储到一个list中
		List<String> detailKeyList = new ArrayList<>();
		for (Long examId : examIdList) {
			detailKeyList.add(getDetailKey(examId));
		}
		//去缓存中获取数据
		List<ExamVO> examVOList = redisService.multiGet(detailKeyList, ExamVO.class);
		CollUtil.removeNull(examVOList);
		if (CollectionUtil.isEmpty(examVOList) || examVOList.size() != examIdList.size()) {
			//说明redis中数据有问题 从数据库中查数据并且重新刷新缓存
			return null;
		}
		return examVOList;
	}

	/**
	 * 获取ExamList在Redis中的Key(根据竞赛类型判断）
	 * @author IhavBB
	 * @date 23:35 2025/3/28
	 * @param examListType 竞赛类型
	 * @param userId 用户ID，竞赛类型为用户的竞赛时需要使用本参数
	 * @return {@link String}
	**/
	private String getExamListKey(Integer examListType, Long userId) {
		if (ExamListType.EXAM_UN_FINISH_LIST.getValue().equals(examListType)) {
			return CacheConstants.EXAM_UNFINISHED_LIST;
		} else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
			return CacheConstants.EXAM_HISTORY_LIST;
		} else {
			return CacheConstants.USER_EXAM_LIST + userId;
		}
	}
	/**
	 * Exam竞赛详情在Redis中的Key
	 * @author IhavBB
	 * @date 23:37 2025/3/28
	 * @param examId
	 * @return {@link String}
	**/
	private String getDetailKey(Long examId) {
		return CacheConstants.EXAM_DETAIL + examId;
	}

	/**
	 * 获取用户的竞赛在Redis中的Key
	 * @author IhavBB
	 * @date 23:46 2025/3/28
	 * @param userId
	 * @return {@link String}
	**/
	private String getUserExamListKey(Long userId) {
		return CacheConstants.USER_EXAM_LIST + userId;
	}
	/**
	 * 竞赛中的问题List在Redis中的Key
	 * @author IhavBB
	 * @date 23:47 2025/3/28
	 * @param examId
	 * @return {@link String}
	**/
	private String getExamQuestionListKey(Long examId) {
		return CacheConstants.EXAM_QUESTION_LIST + examId;
	}

	private String getExamRankListKey(Long examId) {
		return CacheConstants.EXAM_RANK_LIST + examId;
	}
}