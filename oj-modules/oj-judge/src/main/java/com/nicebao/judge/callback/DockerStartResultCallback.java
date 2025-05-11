package com.nicebao.judge.callback;


import cn.hutool.core.util.StrUtil;
import com.nicebao.common.core.enums.CodeRunStatus;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 处理Docker命令执行的输出流和资源统计。
 * @author IhavBB
**/
@Getter
@Setter
public class DockerStartResultCallback extends ExecStartResultCallback {
    //记录执行成功还是失败
    private CodeRunStatus codeRunStatus;
    private String errorMessage;
    private String message;

    /**
     * 重写自父类 ExecStartResultCallback 的方法，用于处理 Docker 命令执行过程中产生的每一帧输出数据。
     * <p>
     * 每当 Docker 容器执行命令产生新的输出时（无论是标准输出还是错误输出），该方法会被自动调用。
     * @author IhavBB
     * @date 11:07 2025/4/13
     * @param frame Docker返回的每一帧数据
     *              其中每一帧数据包含两部分：
     *              流类型StreamType.STDERR和StreamType.STDOUT，分别表示错误输出和标准输出。
     *              数据负载frame.getPayload()返回的是字节数组，需要通过toString()方法转换为字符串。
    **/
    @Override
    public void onNext(Frame frame) {
        StreamType streamType = frame.getStreamType();
        //判断是什么类型的数据，错误类型还是正常类型
        if (StreamType.STDERR.equals(streamType)) {
            //如果是第一次，则直接赋值，否则拼接
            if (StrUtil.isEmpty(errorMessage)) {
                errorMessage = new String(frame.getPayload());
            } else {
                errorMessage = errorMessage + new String(frame.getPayload());
            }
            codeRunStatus = CodeRunStatus.FAILED;
        } else {
            message = new String(frame.getPayload());
            codeRunStatus = CodeRunStatus.SUCCEED;
        }
        super.onNext(frame);
    }
}
