package com.nicebao.system.Service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.system.Service.exam.IExamService;
import com.nicebao.system.domain.exam.Exam;
import com.nicebao.system.domain.exam.ExamQuestion;
import com.nicebao.system.domain.exam.dto.ExamAddDTO;
import com.nicebao.system.domain.exam.dto.ExamEditDTO;
import com.nicebao.system.domain.exam.dto.ExamQueryDTO;
import com.nicebao.system.domain.exam.dto.ExamQuestAddDTO;
import com.nicebao.system.domain.exam.vo.ExamDetailVO;
import com.nicebao.system.domain.exam.vo.ExamVO;
import com.nicebao.system.domain.question.Question;
import com.nicebao.system.domain.question.vo.QuestionVO;
import com.nicebao.system.mapper.exam.ExamMapper;
import com.nicebao.system.mapper.exam.ExamQuestionMapper;
import com.nicebao.system.mapper.question.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.baomidou.mybatisplus.extension.toolkit.Db.saveBatch;

/**
 * IExamServiceImpl
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/19
 */
@Service
public class IExamServiceImpl implements IExamService {
	@Autowired
	private ExamMapper examMapper;
	@Autowired
	private QuestionMapper questionMapper;
	@Autowired
	private ExamQuestionMapper examQuestionMapper;

	@Override
	public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
		PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
		return examMapper.selectExamList(examQueryDTO);
	}

	@Override
	public String add(ExamAddDTO examAddDTO) {
		checkExamSaveParams(examAddDTO, null);
		Exam exam = new Exam();
		BeanUtil.copyProperties(examAddDTO, exam);
		examMapper.insert(exam);
		return exam.getExamId().toString();
	}

	@Override
	public boolean questionAdd(ExamQuestAddDTO examQuestAddDTO) {
		Exam exam = getExam(examQuestAddDTO.getExamId());
		checkExam(exam);
		if (Constants.TRUE.equals(exam.getStatus())) {
			throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
		}
		Set<Long> questionIdSet = examQuestAddDTO.getQuestionIdSet();
		if (CollectionUtil.isEmpty(questionIdSet)) {
			//传入空值不添加，直接返回OK
			return true;
		}
		List<Question> questionList = questionMapper.selectBatchIds(questionIdSet);
		if (CollectionUtil.isEmpty(questionList) || questionList.size() < questionIdSet.size()) {
			throw new ServiceException(ResultCode.EXAM_QUESTION_NOT_EXISTS);
		}
		return saveExamQuestion(exam, questionIdSet);
	}

	@Override
	public int questionDelete(Long examId, Long questionId) {
		Exam exam = getExam(examId);
		checkExam(exam);
		if (Constants.TRUE.equals(exam.getStatus())) {
			throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
		}
		return examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
				.eq(ExamQuestion::getExamId, examId)
				.eq(ExamQuestion::getQuestionId, questionId));
	}

	@Override
	public ExamDetailVO detail(Long examId) {
		ExamDetailVO examDetailVO = new ExamDetailVO();
		Exam exam = getExam(examId);
		BeanUtil.copyProperties(exam, examDetailVO);
		List<QuestionVO> questionVOList = examQuestionMapper.selectExamQuestionList(examId);
		//如果竞赛里没题目，直接返回
		if (CollectionUtil.isEmpty(questionVOList)) {
			return examDetailVO;
		}
		//插入竞赛中的题目
		examDetailVO.setExamQuestionList(questionVOList);
		return examDetailVO;
	}

	@Override
	public int edit(ExamEditDTO examEditDTO) {
		Exam exam = getExam(examEditDTO.getExamId());
		if (Constants.TRUE.equals(exam.getStatus())) {
			throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
		}
		//检查一下准备修改的竞赛是否已经开始。
		checkExam(exam);
		//校验用户要输入的数据是否合法？
		checkExamSaveParams(examEditDTO, examEditDTO.getExamId());
		exam.setTitle(examEditDTO.getTitle());
		exam.setStartTime(examEditDTO.getStartTime());
		exam.setEndTime(examEditDTO.getEndTime());
		return examMapper.updateById(exam);
	}

	@Override
	public int delete(Long examId) {
		Exam exam = getExam(examId);
		if (Constants.TRUE.equals(exam.getStatus())) {
			throw new ServiceException(ResultCode.EXAM_IS_PUBLISH);
		}
		checkExam(exam);
		examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
				.eq(ExamQuestion::getExamId, examId));
		return examMapper.deleteById(exam);
	}



	/**
	 * 保存题目到竞赛
	 * @author IhavBB
	 * @date 22:47 2025/3/20
	 * @param exam
	 * @param questionIdSet
	 * @return {@link boolean}
	**/
	private boolean saveExamQuestion(Exam exam, Set<Long> questionIdSet) {
		int num = 1;
		List<ExamQuestion> examQuestionList = new ArrayList<>();
		for (Long questionId : questionIdSet) {
			ExamQuestion examQuestion = new ExamQuestion();
			examQuestion.setExamId(exam.getExamId());
			examQuestion.setQuestionId(questionId);
			examQuestion.setQuestionOrder(num++);
			examQuestionList.add(examQuestion);
		}
		return saveBatch(examQuestionList);
	}


	/**
	 * 校验竞赛合法性。检测竞赛是否已经开始，已经开始的竞赛不能修改
	 * @author IhavBB
	 * @date 22:07 2025/3/20
	 * @param exam
	**/
	private void checkExam(Exam exam) {
		if (exam.getStartTime().isBefore(LocalDateTime.now())) {
			throw new ServiceException(ResultCode.EXAM_STARTED);
		}
	}

	/**
	 * 检查竞赛是否存在
	 * @author IhavBB
	 * @date 22:07 2025/3/20
	 * @param examId
	 * @return {@link Exam}
	**/
	private Exam getExam(Long examId) {
		Exam exam = examMapper.selectById(examId);
		if (exam == null) {
			throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
		}
		return exam;
	}

	/**
	 * 竞赛保存时参数校验
	 * @author IhavBB
	 * @date 23:44 2025/3/20
	 * @param examSaveDTO
	 * @param examId
	**/
	private void checkExamSaveParams(ExamAddDTO examSaveDTO, Long examId) {
		//1、竞赛标题是否重复进行判断   2、竞赛开始、结束时间进行判断
		List<Exam> examList = examMapper
				.selectList(new LambdaQueryWrapper<Exam>()
						.eq(Exam::getTitle, examSaveDTO.getTitle())
						.ne(examId != null, Exam::getExamId, examId));
		if (CollectionUtil.isNotEmpty(examList)) {
			throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
		}
		if (examSaveDTO.getStartTime().isBefore(LocalDateTime.now())) {
			//竞赛开始时间不能早于当前时间
			throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
		}
		if (examSaveDTO.getStartTime().isAfter(examSaveDTO.getEndTime())) {
			throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
		}
	}
}
