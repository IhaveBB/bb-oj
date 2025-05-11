package com.nicebao.api.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicebao.api.domain.UserExeResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserQuestionResultVO {
    //是够通过标识
    // 0  未通过  1 通过
    private Integer pass;
    //异常信息
    private String exeMessage;

    private List<UserExeResult> userExeResultList;

    @JsonIgnore
    private Integer score;
}
