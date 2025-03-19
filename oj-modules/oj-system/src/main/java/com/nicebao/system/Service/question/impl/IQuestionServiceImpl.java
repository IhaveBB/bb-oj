package com.nicebao.system.Service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.system.Service.question.IQuestionService;
import com.nicebao.system.domain.question.Question;
import com.nicebao.system.domain.question.dto.QuestionAddDTO;
import com.nicebao.system.domain.question.dto.QuestionEditDTO;
import com.nicebao.system.domain.question.dto.QuestionQueryDTO;
import com.nicebao.system.domain.question.vo.QuestionDetailVO;
import com.nicebao.system.domain.question.vo.QuestionVO;
import com.nicebao.system.mapper.question.QuestionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * IQuestionServiceImpl
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/24
 */
@Service
@Slf4j
public class IQuestionServiceImpl implements IQuestionService {

	@Autowired
	private QuestionMapper questionMapper;

	@Override
	public List<QuestionVO> list(QuestionQueryDTO questionQueryDTO) {
		String excludeIdStr = questionQueryDTO.getExcludeIdStr();
		if (StrUtil.isNotEmpty(excludeIdStr)) {
			String[] excludeIdArr = excludeIdStr.split(Constants.SPLIT_SEM);
			Set<Long> excludeIdSet = Arrays.stream(excludeIdArr)
					.map(Long::valueOf)
					.collect(Collectors.toSet());
			questionQueryDTO.setExcludeIdSet(excludeIdSet);
		}
		/**
		 * 底层原理
		 * <p>
		 * 通过 ThreadLocal 在当前线程绑定分页参数（pageNum=1, pageSize=10），
		 * MyBatis 执行 SQL 时会自动添加 LIMIT 10 OFFSET 0
		**/
		PageHelper.startPage(questionQueryDTO.getPageNum(), questionQueryDTO.getPageSize());
		List<QuestionVO> questionVOList = questionMapper.selectQuestionList(questionQueryDTO);
		return questionVOList;

	}

	@Override
	public boolean add(QuestionAddDTO questionAddDTO) {
		//以标题判断，是否重复添加
		List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
				.eq(Question::getTitle, questionAddDTO.getTitle()));
		if (CollectionUtil.isNotEmpty(questionList)) {
			throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
		}
		Question question = new Question();
		//将 DTO 转换为实体对象并插入数据库。
		BeanUtil.copyProperties(questionAddDTO, question);
		int insert = questionMapper.insert(question);
		if (insert <= 0) {
			return false;
		}
		//todo 这里回来写ES的逻辑
		//todo 这里回来写缓存逻辑
		return false;
	}

	@Override
	public QuestionDetailVO detail(Long questionId) {
		Question question = questionMapper.selectById(questionId);
		if (question == null) {
			throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
		}
		QuestionDetailVO questionDetailVO = new QuestionDetailVO();
		BeanUtil.copyProperties(question, questionDetailVO);
		return questionDetailVO;
	}

	@Override
	public int edit(QuestionEditDTO questionEditDTO) {
		Question oldQuestion = questionMapper.selectById(questionEditDTO.getQuestionId());
		if (oldQuestion == null) {
			throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
		}
		oldQuestion.setTitle(questionEditDTO.getTitle());
		oldQuestion.setDifficulty(questionEditDTO.getDifficulty());
		oldQuestion.setTimeLimit(questionEditDTO.getTimeLimit());
		oldQuestion.setSpaceLimit(questionEditDTO.getSpaceLimit());
		oldQuestion.setContent(questionEditDTO.getContent());
		oldQuestion.setQuestionCase(questionEditDTO.getQuestionCase());
		oldQuestion.setDefaultCode(questionEditDTO.getDefaultCode());
		oldQuestion.setMainFuc(questionEditDTO.getMainFuc());
		//todo 这里后面实现更新ES的逻辑
		return questionMapper.updateById(oldQuestion);
	}

	@Override
	public int delete(Long questionId) {
		Question question = questionMapper.selectById(questionId);
		if (question == null) {
			throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
		}
		//todo 更新ES
		//todo 更新缓存
		return questionMapper.deleteById(questionId);
	}
}
