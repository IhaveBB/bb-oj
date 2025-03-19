package com.nicebao.system.Service.exam;

import com.nicebao.system.domain.exam.dto.ExamAddDTO;
import com.nicebao.system.domain.exam.dto.ExamQueryDTO;
import com.nicebao.system.domain.exam.vo.ExamVO;

import java.util.List;

/**
 * IExamService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/19
 */
public interface IExamService {
	List<ExamVO> list(ExamQueryDTO examQueryDTO);

	String add(ExamAddDTO examAddDTO);

}
