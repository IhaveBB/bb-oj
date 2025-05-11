package com.nicebao.judge.service;


import com.nicebao.judge.domain.SandBoxExecuteResult;

import java.util.List;

public interface ISandboxPoolService {
    SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList);
}
