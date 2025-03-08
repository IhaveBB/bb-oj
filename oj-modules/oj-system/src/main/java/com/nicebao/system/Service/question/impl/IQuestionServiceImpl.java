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
import com.nicebao.system.domain.question.dto.QuestionQueryDTO;
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

		return false;
	}
}
