package com.nicebao.job.service;


import com.nicebao.job.domain.message.MessageText;

import java.util.List;

public interface IMessageTextService {

    boolean batchInsert(List<MessageText> messageTextList);
}
