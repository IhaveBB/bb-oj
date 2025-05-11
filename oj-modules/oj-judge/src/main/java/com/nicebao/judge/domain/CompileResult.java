package com.nicebao.judge.domain;

import lombok.Data;


@Data
public class CompileResult {
    //编译是否成功
    private boolean compiled;
    //编译输出信息 （错误信息）
    private String exeMessage;
}
