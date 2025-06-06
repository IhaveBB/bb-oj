package com.nicebao.job.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nicebao.job.domain.message.Message;
import com.nicebao.job.mapper.message.MessageMapper;
import com.nicebao.job.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Override
    public boolean batchInsert(List<Message> messageList) {
        return saveBatch(messageList);
    }
}
