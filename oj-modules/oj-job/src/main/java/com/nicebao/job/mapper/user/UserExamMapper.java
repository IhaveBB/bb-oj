package com.nicebao.job.mapper.user;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nicebao.job.domain.user.UserExam;
import com.nicebao.job.domain.user.UserScore;

import java.util.List;


public interface UserExamMapper extends BaseMapper<UserExam> {

    void updateUserScoreAndRank(List<UserScore> userScoreList);
}
