package com.nicebao.judge.domain;

import com.nicebao.common.core.enums.CodeRunStatus;
import lombok.Data;

import java.util.List;

@Data
public class SandBoxExecuteResult {
    //执行结果
    private CodeRunStatus runStatus;
    //异常信息
    private String exeMessage;
    //执行结果
    private List<String> outputList;
    //占用内存  kb
    private Long useMemory;
    //消耗时间   ms
    private Long useTime;

    public static SandBoxExecuteResult fail(CodeRunStatus runStatus, String errorMsg) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setExeMessage(errorMsg);
        return result;
    }

    public static SandBoxExecuteResult fail(CodeRunStatus runStatus) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setExeMessage(runStatus.getMsg());
        return result;
    }

    public static SandBoxExecuteResult fail(CodeRunStatus runStatus, List<String> outputList,
                                            Long useMemory, Long useTime) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setExeMessage(runStatus.getMsg());
        result.setOutputList(outputList);
        result.setUseMemory(useMemory);
        result.setUseTime(useTime);
        return result;
    }

    public static SandBoxExecuteResult success(CodeRunStatus runStatus, List<String> outputList,
                                            Long useMemory, Long useTime) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setOutputList(outputList);
        result.setUseMemory(useMemory);
        result.setUseTime(useTime);
        return result;
    }
}
