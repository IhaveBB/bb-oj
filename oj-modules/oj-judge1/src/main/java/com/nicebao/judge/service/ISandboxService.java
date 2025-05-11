package com.nicebao.judge.service;


import com.nicebao.judge.domain.SandBoxExecuteResult;

import java.util.List;

public interface ISandboxService {
    SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList);
}
