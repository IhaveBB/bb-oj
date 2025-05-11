package com.nicebao.friend.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.friend.domain.question.Question;
import com.nicebao.friend.domain.question.dto.QuestionQueryDTO;
import com.nicebao.friend.domain.question.es.QuestionES;
import com.nicebao.friend.domain.question.vo.QuestionDetailVO;
import com.nicebao.friend.domain.question.vo.QuestionVO;
import com.nicebao.friend.elasticsearch.QuestionRepository;
import com.nicebao.friend.manager.QuestionCacheManager;
import com.nicebao.friend.mapper.question.QuestionMapper;
import com.nicebao.friend.mapper.user.UserSubmitMapper;
import com.nicebao.friend.service.question.IQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * QuestionServiceImpl
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/12
 */
@Service
@Slf4j
public class QuestionServiceImpl implements IQuestionService {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private QuestionMapper questionMapper;

	@Autowired
	private UserSubmitMapper userSubmitMapper;

	@Autowired
	private QuestionCacheManager questionCacheManager;

	/**
	 * 获取题目列表
	 * @author IhavBB
	 * @date 17:34 2025/4/12
	 * @param questionQueryDTO
	 * @return {@link TableDataInfo}
	**/
	@Override
	public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
		//查询es中题目个数，没有则刷新ES
		long count = questionRepository.count();
		if (count <= 0) {
			refreshQuestion();
		}
		//创建分页参数（ES 的分页从 0 开始，所以要减 1）
		Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
		Pageable pageable = PageRequest.of(questionQueryDTO.getPageNum() - 1, questionQueryDTO.getPageSize(), sort);

		Integer difficulty = questionQueryDTO.getDifficulty();
		String keyword = questionQueryDTO.getKeyword();
		Page<QuestionES> questionESPage;
		//根据条件调用不同的 ES 查询方法
		if (difficulty == null && StrUtil.isEmpty(keyword)) {
			// 无条件查询：获取全部
			questionESPage = questionRepository.findAll(pageable);
		} else if (StrUtil.isEmpty(keyword)) {
			//按难度查询
			questionESPage = questionRepository.findQuestionByDifficulty(difficulty, pageable);
		} else if (difficulty == null) {
			//按关键词查询
			questionESPage = questionRepository.findByTitleOrContent(keyword, keyword, pageable);
		} else {
			//同时按关键词和难度查询
			questionESPage = questionRepository.findByTitleOrContentAndDifficulty(keyword, keyword, difficulty, pageable);
		}
		//异常处理
		long total = questionESPage.getTotalElements();
		if (total <= 0) {
			return TableDataInfo.empty();
		}
		// 将 ES 对象转换为前端需要的 VO 对象
		List<QuestionES> questionESList = questionESPage.getContent();
		List<QuestionVO> questionVOList = BeanUtil.copyToList(questionESList, QuestionVO.class);
		return TableDataInfo.success(questionVOList, total);
	}

	/**
	 * 热门题目列表
	 * @author IhavBB
	 * @date 17:42 2025/4/12
	 * @return {@link List< QuestionVO>}
	**/
	@Override
	public List<QuestionVO> hotList() {
		//从缓存获取热门题目数量
		Long total = questionCacheManager.getHostListSize();
		List<Long> hotQuestionIdList;
		//不够则查表刷新缓存，否则直接返回数据
		if (total == null || total <= 0) {
			PageHelper.startPage(Constants.HOST_QUESTION_LIST_START, Constants.HOST_QUESTION_LIST_END);
			hotQuestionIdList = userSubmitMapper.selectHostQuestionList();
			questionCacheManager.refreshHotQuestionList(hotQuestionIdList);
		} else {
			hotQuestionIdList = questionCacheManager.getHostList();
		}
		return assembleQuestionVOList(hotQuestionIdList);
	}
	/**
	 * 查看问题详情，优先从 ES 查询，未命中则查数据库。
	 * @author IhavBB
	 * @date 17:47 2025/4/12
	 * @param questionId
	 * @return {@link QuestionDetailVO}
	**/
	@Override
	public QuestionDetailVO detail(Long questionId) {
		QuestionES questionES = questionRepository.findById(questionId).orElse(null);
		QuestionDetailVO questionDetailVO = new QuestionDetailVO();
		//先查ES
		if (questionES != null) {
			BeanUtil.copyProperties(questionES, questionDetailVO);
			return questionDetailVO;
		}
		//es查不到去数据库查
		Question question = questionMapper.selectById(questionId);
		if (question == null) {
			return null;
		}
		refreshQuestion();
		BeanUtil.copyProperties(question, questionDetailVO);
		return questionDetailVO;
	}

	/**
	 * 查找上一个问题ID
	 * @author IhavBB
	 * @date 8:21 2025/4/13
	 * @param questionId 当前问题ID
	 * @return {@link String}
	**/
	@Override
	public String preQuestion(Long questionId) {
		Long listSize = questionCacheManager.getListSize();
		if (listSize == null || listSize <= 0) {
			questionCacheManager.refreshCache();
		}
		return questionCacheManager.preQuestion(questionId).toString();
	}

	/**
	 * 查找在一个问题ID
	 * @author IhavBB
	 * @date 8:21 2025/4/13
	 * @param questionId 当前问题ID
	 * @return {@link String}
	**/
	@Override
	public String nextQuestion(Long questionId) {
		Long listSize = questionCacheManager.getListSize();
		if (listSize == null || listSize <= 0) {
			questionCacheManager.refreshCache();
		}
		return questionCacheManager.nextQuestion(questionId).toString();
	}

	/**
	 * 刷新ES数据
	 * @author IhavBB
	 * @date 8:22 2025/4/13
	**/
	private void refreshQuestion() {
		// 从数据库查询所有问题
		List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>());
		if (CollectionUtil.isEmpty(questionList)) {
			return;
		}
		//转为ES对象并保存到ES
		List<QuestionES> questionESList = BeanUtil.copyToList(questionList, QuestionES.class);
		questionRepository.saveAll(questionESList);
	}
	/**
	 * 封装热门题目列表成QuestionList
	 * 利用detail去查详细信息
	 * @author IhavBB
	 * @date 8:23 2025/4/13
	 * @param hotQuestionIdList 热门题目IdList
	 * @return {@link List< QuestionVO>}
	**/
	private List<QuestionVO> assembleQuestionVOList(List<Long> hotQuestionIdList) {
		if (CollectionUtil.isEmpty(hotQuestionIdList)) {
			return new ArrayList<>();
		}
		List<QuestionVO> resultList = new ArrayList<>();
		for (Long questionId : hotQuestionIdList) {
			QuestionVO questionVO = new QuestionVO();
			QuestionDetailVO detail = detail(questionId);
			questionVO.setTitle(detail.getTitle());
			resultList.add(questionVO);
		}
		return resultList;
	}
}
