package com.nicebao.friend.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.common.core.enums.ExamListType;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.core.utils.ThreadLocalUtil;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.common.security.service.TokenService;
import com.nicebao.friend.domain.user.UserExam;
import com.nicebao.friend.domain.exam.Exam;
import com.nicebao.friend.domain.exam.dto.ExamQueryDTO;
import com.nicebao.friend.domain.exam.vo.ExamVO;
import com.nicebao.friend.manager.ExamCacheManager;
import com.nicebao.friend.mapper.exam.ExamMapper;
import com.nicebao.friend.mapper.user.UserExamMapper;
import com.nicebao.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserExamServiceImpl
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/1
 */
@Service
public class UserExamServiceImpl implements IUserExamService {

	@Autowired
	private ExamMapper examMapper;

	@Autowired
	private UserExamMapper userExamMapper;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private ExamCacheManager examCacheManager;

	@Value("${jwt.secret}")
	private String secret;

	@Override
	public int enter(String token, Long examId) {
		Exam exam = examMapper.selectById(examId);
		if (exam == null) {
			throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
		}
		if(exam.getStartTime().isBefore(LocalDateTime.now())) {
			throw new ServiceException(ResultCode.EXAM_STARTED);
		}

		Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
		UserExam userExam = userExamMapper.selectOne(new LambdaQueryWrapper<UserExam>()
				.eq(UserExam::getExamId, examId)
				.eq(UserExam::getUserId, userId));
		if (userExam != null) {
			throw new ServiceException(ResultCode.USER_EXAM_HAS_ENTER);
		}
		examCacheManager.addUserExamCache(userId, examId);
		userExam = new UserExam();
		userExam.setExamId(examId);
		userExam.setUserId(userId);
		return userExamMapper.insert(userExam);
	}

	//先查询缓存（u:e:l:用户id）  如果缓存能够查询到
	//如果查询不到   数据库当中再去查询  并且将数据库中的数据同步给redis
	@Override
	public TableDataInfo list(ExamQueryDTO examQueryDTO) {
		Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
		examQueryDTO.setType(ExamListType.USER_EXAM_LIST.getValue());
		Long total = examCacheManager.getListSize(ExamListType.USER_EXAM_LIST.getValue(), userId);
		List<ExamVO> examVOList;
		if (total == null || total <= 0) {
			//从数据库中查询我的竞赛列表
			PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
			examVOList = userExamMapper.selectUserExamList(userId);
			examCacheManager.refreshCache(ExamListType.USER_EXAM_LIST.getValue(), userId);
			total = new PageInfo<>(examVOList).getTotal();
		} else {
			examVOList = examCacheManager.getExamVOList(examQueryDTO, userId);
			total = examCacheManager.getListSize(examQueryDTO.getType(), userId);
		}
		if (CollectionUtil.isEmpty(examVOList)) {
			return TableDataInfo.empty();
		}
		return TableDataInfo.success(examVOList, total);
	}
}
