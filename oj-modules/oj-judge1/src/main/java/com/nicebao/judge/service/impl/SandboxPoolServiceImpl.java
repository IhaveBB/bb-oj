package com.nicebao.judge.service.impl;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.constants.JudgeConstants;
import com.nicebao.common.core.enums.CodeRunStatus;
import com.nicebao.judge.callback.DockerStartResultCallback;
import com.nicebao.judge.callback.StatisticsCallback;
import com.nicebao.judge.config.DockerSandBoxPool;
import com.nicebao.judge.domain.CompileResult;
import com.nicebao.judge.domain.SandBoxExecuteResult;
import com.nicebao.judge.service.ISandboxPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SandboxPoolServiceImpl implements ISandboxPoolService {

    @Autowired
    private DockerSandBoxPool sandBoxPool;

    @Autowired
    private DockerClient dockerClient;

    private String containerId;

    private String userCodeFileName;

    @Value("${sandbox.limit.time:5}")
    private Long timeLimit;
    /**
     * 执行用户提交的 Java 代码。
     * @author IhavBB
     * @date 21:59 2025/4/13
     * @param userId
     * @param userCode
     * @param inputList
     * @return {@link SandBoxExecuteResult}
    **/
    @Override
    public SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList) {
        containerId = sandBoxPool.getContainer();
        //用户代码保存到容器文件中
        createUserCodeFile(userCode);
        //编译代码
        CompileResult compileResult = compileCodeByDocker();
        if (!compileResult.isCompiled()) {
            sandBoxPool.returnContainer(containerId);
            deleteUserCodeFile();
            return SandBoxExecuteResult.fail(CodeRunStatus.COMPILE_FAILED, compileResult.getExeMessage());
        }
        //执行代码
        return executeJavaCodeByDocker(inputList);
    }

    /**
     * 创建并返回用户代码的文件
     * @author IhavBB
     * @date 21:34 2025/4/13
     * @param userCode 用户代码
    **/
    private void createUserCodeFile(String userCode) {
        String codeDir = sandBoxPool.getCodeDir(containerId);
        log.info("user-pool路径信息：{}", codeDir);
        userCodeFileName = codeDir + File.separator + JudgeConstants.USER_CODE_JAVA_CLASS_NAME;
        //如果文件之前存在，将之前的文件删除掉
        if (FileUtil.exist(userCodeFileName)) {
            FileUtil.del(userCodeFileName);
        }
        FileUtil.writeString(userCode, userCodeFileName, Constants.UTF8);
    }

    /**
     * 使用Docker编译
     * <p>
     * 在 Docker 容器中使用 javac 命令编译用户代码。
     * @author IhavBB
     * @date 21:41 2025/4/13
     * @return {@link CompileResult}
    **/
    private CompileResult compileCodeByDocker() {
        String cmdId = createExecCmd(JudgeConstants.DOCKER_JAVAC_CMD, null, containerId);
        DockerStartResultCallback resultCallback = new DockerStartResultCallback();
        CompileResult compileResult = new CompileResult();
        try {
            dockerClient.execStartCmd(cmdId)
                    .exec(resultCallback)
                    .awaitCompletion();
            if (CodeRunStatus.FAILED.equals(resultCallback.getCodeRunStatus())) {
                compileResult.setCompiled(false);
                compileResult.setExeMessage(resultCallback.getErrorMessage());
            } else {
                compileResult.setCompiled(true);
            }
            return compileResult;
        } catch (InterruptedException e) {
            //此处可以直接抛出 已做统一异常处理  也可再做定制化处理
            throw new RuntimeException(e);
        }
    }
    /**
     * 执行编译好的代码
     * @author IhavBB
     * @date 22:05 2025/4/13
     * @param inputList
     * @return {@link SandBoxExecuteResult}
    **/
    private SandBoxExecuteResult executeJavaCodeByDocker(List<String> inputList) {
        List<String> outList = new ArrayList<>(); //记录输出结果
        long maxMemory = 0L;  //最大占用内存
        long maxUseTime = 0L; //最大运行时间
        //执行用户代码
        for (String inputArgs : inputList) {
            String cmdId = createExecCmd(JudgeConstants.DOCKER_JAVA_EXEC_CMD, inputArgs, containerId);
            //执行代码
            StopWatch stopWatch = new StopWatch();        //执行代码后开始计时
            //执行情况监控
            StatsCmd statsCmd = dockerClient.statsCmd(containerId); //启动监控
            StatisticsCallback statisticsCallback = statsCmd.exec(new StatisticsCallback());
            stopWatch.start();
            DockerStartResultCallback resultCallback = new DockerStartResultCallback();
            try {
                dockerClient.execStartCmd(cmdId)
                        .exec(resultCallback)
                        .awaitCompletion(timeLimit, TimeUnit.SECONDS);
                if (CodeRunStatus.FAILED.equals(resultCallback.getCodeRunStatus())) {
                    //未通过所有用例返回结果
                    return SandBoxExecuteResult.fail(CodeRunStatus.NOT_ALL_PASSED);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            stopWatch.stop();  //结束时间统计
            statsCmd.close();  //结束docker容器执行统计
            //执行耗时
            long userTime = stopWatch.getLastTaskTimeMillis();
            //记录最大的执行用例耗时
            maxUseTime = Math.max(userTime, maxUseTime);
            Long memory = statisticsCallback.getMaxMemory();
            if (memory != null) {
                //记录最大的执行用例占用内存
                maxMemory = Math.max(maxMemory, statisticsCallback.getMaxMemory());
            }
            outList.add(resultCallback.getMessage().trim());   //记录正确的输出结果
        }
        sandBoxPool.returnContainer(containerId);
        deleteUserCodeFile(); //清理文件

        return getSanBoxResult(inputList, outList, maxMemory, maxUseTime); //封装结果
    }

    /**
     * 构造并在 Docker 容器中创建一个可执行命令。
     * @author IhavBB
     * @date 22:02 2025/4/13
     * @param javaCmdArr
     * @param inputArgs
     * @param containerId
     * @return {@link String}
    **/
    private String createExecCmd(String[] javaCmdArr, String inputArgs, String containerId) {
        if (!StrUtil.isEmpty(inputArgs)) {
            //当入参不为空时拼接入参
            String[] inputArray = inputArgs.split(" ");
            javaCmdArr = ArrayUtil.append(JudgeConstants.DOCKER_JAVA_EXEC_CMD, inputArray);
        }
        ExecCreateCmdResponse cmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(javaCmdArr)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .exec();
        return cmdResponse.getId();
    }

    private SandBoxExecuteResult getSanBoxResult(List<String> inputList, List<String> outList,
                                                 long maxMemory, long maxUseTime) {
        if (inputList.size() != outList.size()) {
            //输入用例数量 不等于 输出用例数量  属于执行异常
            return SandBoxExecuteResult.fail(CodeRunStatus.NOT_ALL_PASSED, outList, maxMemory, maxUseTime);
        }
        return SandBoxExecuteResult.success(CodeRunStatus.SUCCEED, outList, maxMemory, maxUseTime);
    }

    private void deleteUserCodeFile() {
        FileUtil.del(userCodeFileName);
    }
}
