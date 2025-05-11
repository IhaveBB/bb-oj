package com.nicebao.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nicebao.job.domain.message.MessageText;
import com.nicebao.job.mapper.message.MessageTextMapper;
import com.nicebao.job.service.IMessageTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MessageTextServiceImpl extends ServiceImpl<MessageTextMapper, MessageText> implements IMessageTextService {

    @Override
    public boolean batchInsert(List<MessageText> messageTextList) {
        return saveBatch(messageTextList);
    }
}
