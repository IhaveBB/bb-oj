package com.nicebao.system.Service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.system.Service.exam.IExamService;
import com.nicebao.system.domain.exam.Exam;
import com.nicebao.system.domain.exam.dto.ExamAddDTO;
import com.nicebao.system.domain.exam.dto.ExamQueryDTO;
import com.nicebao.system.domain.exam.vo.ExamVO;
import com.nicebao.system.mapper.exam.ExamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
			throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);  //竞赛开始时间不能早于当前时间
		}
		if (examSaveDTO.getStartTime().isAfter(examSaveDTO.getEndTime())) {
			throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
		}
}
