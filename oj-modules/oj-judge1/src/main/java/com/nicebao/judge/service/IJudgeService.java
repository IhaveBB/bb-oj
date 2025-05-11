package com.nicebao.judge.service;


import com.nicebao.api.domain.dto.JudgeSubmitDTO;
import com.nicebao.api.domain.vo.UserQuestionResultVO;

public interface IJudgeService {
    UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO);
}
