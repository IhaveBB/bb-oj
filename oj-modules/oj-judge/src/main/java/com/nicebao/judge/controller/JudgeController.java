package com.nicebao.judge.controller;

import com.nicebao.api.domain.dto.JudgeSubmitDTO;
import com.nicebao.api.domain.vo.UserQuestionResultVO;
import com.nicebao.common.core.controller.BaseController;
import com.nicebao.common.core.domain.R;
import com.nicebao.judge.service.IJudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 判题请求入口
 * @author IhavBB
**/
@RestController
@RequestMapping("/judge")
@Slf4j
public class JudgeController extends BaseController {

    @Autowired
    private IJudgeService judgeService;

    @PostMapping("/doJudgeJavaCode")
    public R<UserQuestionResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO) {
        return R.ok(judgeService.doJudgeJavaCode(judgeSubmitDTO));
    }
}
