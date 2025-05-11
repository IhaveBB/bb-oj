package com.nicebao.api.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserExeResult {

    private String input;
    //期望输出
    private String output;
    //实际输出
    private String exeOutput;
}
