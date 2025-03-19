package com.nicebao.system.mapper.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nicebao.system.domain.exam.Exam;
import com.nicebao.system.domain.exam.dto.ExamQueryDTO;
import com.nicebao.system.domain.exam.vo.ExamVO;

import java.util.List;

/**
 * ExamMapper
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/19
 */
public interface ExamMapper extends BaseMapper<Exam> {

	List<ExamVO> selectExamList(ExamQueryDTO examQueryDTO);

}
