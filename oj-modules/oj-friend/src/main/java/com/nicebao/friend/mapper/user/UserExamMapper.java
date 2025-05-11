package com.nicebao.friend.mapper.user;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nicebao.friend.domain.user.UserExam;
import com.nicebao.friend.domain.exam.vo.ExamRankVO;
import com.nicebao.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface UserExamMapper extends BaseMapper<UserExam> {

    List<ExamVO> selectUserExamList(Long userId);

    List<ExamRankVO> selectExamRankList(Long examId);

}