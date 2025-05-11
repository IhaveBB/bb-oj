package com.nicebao.friend.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.domain.PageQueryDTO;
import com.nicebao.common.core.domain.TableDataInfo;
import com.nicebao.common.core.utils.ThreadLocalUtil;
import com.nicebao.friend.domain.message.vo.MessageTextVO;
import com.nicebao.friend.manager.MessageCacheManager;
import com.nicebao.friend.mapper.message.MessageTextMapper;
import com.nicebao.friend.service.user.IUserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserMessageServiceImpl
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/4
 */
@Service
@Slf4j
public class UserMessageServiceImpl implements IUserMessageService {
	@Autowired
	private MessageCacheManager messageCacheManager;

	@Autowired
	private MessageTextMapper messageTextMapper;

	@Override
	public TableDataInfo list(PageQueryDTO dto) {
		Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
		//从缓存中查和自己有关的消息Id数量
		Long total = messageCacheManager.getListSize(userId);
		List<MessageTextVO> messageTextVOList;
		//如果从缓存中没查出来，则取数据库里查
		if (total == null || total <= 0) {
			//从数据库中查询我的竞赛列表
			PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
			messageTextVOList = messageTextMapper.selectUserMsgList(userId);
			messageCacheManager.refreshCache(userId);
			total = new PageInfo<>(messageTextVOList).getTotal();
		} else {
			//如果缓存中查出来，则直接去缓存获取
			messageTextVOList = messageCacheManager.getMsgTextVOList(dto, userId);
		}
		if (CollectionUtil.isEmpty(messageTextVOList)) {
			return TableDataInfo.empty();
		}
		return TableDataInfo.success(messageTextVOList, total);
	}

}
