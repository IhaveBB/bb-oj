package com.nicebao.system.Service.question.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.system.Service.question.IQuestionService;
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
}
