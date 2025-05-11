package com.nicebao.friend.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.nicebao.common.core.constants.CacheConstants;
import com.nicebao.common.core.domain.PageQueryDTO;
import com.nicebao.common.redis.service.RedisService;
import com.nicebao.friend.domain.message.vo.MessageTextVO;
import com.nicebao.friend.mapper.message.MessageTextMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageCacheManager
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/4
 */

@Component
public class MessageCacheManager {

	@Autowired
	private RedisService redisService;

	@Autowired
	private MessageTextMapper messageTextMapper;

	public Long getListSize(Long userId) {
		String userMsgListKey = getUserMsgListKey(userId);
		return redisService.getListSize(userMsgListKey);
	}

	/**
	 * u:m:l:用户Id
	 * @author IhavBB
	 * @date 22:48 2025/4/4
	 * @param userId
	 * @return {@link String}
	**/
	private String getUserMsgListKey(Long userId) {
		return CacheConstants.USER_MESSAGE_LIST + userId;
	}

	/**
	 * m:d：消息Id
	 * @author IhavBB
	 * @date 22:49 2025/4/4
	 * @param textId
	 * @return {@link String}
	**/
	private String getMsgDetailKey(Long textId) {
		return CacheConstants.MESSAGE_DETAIL + textId;
	}

	/**
	 * 刷新缓存
	 * 从数据看中取出数据，并存入Redis
	 * @author IhavBB
	 * @date 22:47 2025/4/4
	 * @param userId
	**/
	public void refreshCache(Long userId) {
		List<MessageTextVO> messageTextVOList = messageTextMapper.selectUserMsgList(userId);
		if (CollectionUtil.isEmpty(messageTextVOList)) {
			return;
		}
		//获取出testId列表
		List<Long> textIdList = messageTextVOList.stream().map(MessageTextVO::getTextId).toList();
		String userMsgListKey = getUserMsgListKey(userId);
		//u:m:l:用户Id    消息Id
		redisService.rightPushAll(userMsgListKey, textIdList);
		//m:d:消息id		详细消息内容
		Map<String, MessageTextVO> messageTextVOMap = new HashMap<>();
		for (MessageTextVO messageTextVO : messageTextVOList) {
			messageTextVOMap.put(getMsgDetailKey(messageTextVO.getTextId()), messageTextVO);
		}
		redisService.multiSet(messageTextVOMap);
	}

	/**
	 * 从缓存获取msgTextVOList
	 * @author IhavBB
	 * @date 15:42 2025/4/4
	 * @param dto 分页信息
	 * @param userId 用户Id
	 * @return {@link List< MessageTextVO>}
	**/
	public List<MessageTextVO> getMsgTextVOList(PageQueryDTO dto, Long userId) {
		int start = (dto.getPageNum() - 1) * dto.getPageSize();
		//下标需要 -1
		int end = start + dto.getPageSize() - 1;
		//先根据用户ID查询来有哪些相关的msgTestId，
		String userMsgListKey = getUserMsgListKey(userId);
		List<Long> msgTextIdList = redisService.getCacheListByRange(userMsgListKey, start, end, Long.class);
		//再根据查出来的msgTextIdList查询出对应的msgTextVOList
		List<MessageTextVO> messageTextVOList = assembleMsgTextVOList(msgTextIdList);
		if (CollectionUtil.isEmpty(messageTextVOList)) {
			//说明redis中数据可能有问题 从数据库中查数据并且重新刷新缓存
			messageTextVOList = getMsgTextVOListByDB(dto, userId); //从数据库中获取数据
			refreshCache(userId);
		}
		return messageTextVOList;
	}

	/**
	 * 处理从Redis中取出的数据，根据传入的msgTestIdList，去查询出具体的消息
	 * @author IhavBB
	 * @date 14:39 2025/4/4
	 * @param msgTextIdList 消息IdList
	 * @return {@link List< MessageTextVO>}
	 **/
	private List<MessageTextVO> assembleMsgTextVOList(List<Long> msgTextIdList) {
		if (CollectionUtil.isEmpty(msgTextIdList)) {
			//说明redis当中没数据 从数据库中查数据并且重新刷新缓存
			return null;
		}
		//拼接redis当中key的方法 并且将拼接好的key存储到一个list中
		List<String> detailKeyList = new ArrayList<>();
		for (Long textId : msgTextIdList) {
			detailKeyList.add(getMsgDetailKey(textId));
		}
		List<MessageTextVO> messageTextVOList = redisService.multiGet(detailKeyList, MessageTextVO.class);
		CollUtil.removeNull(messageTextVOList);
		if (CollectionUtil.isEmpty(messageTextVOList) || messageTextVOList.size() != msgTextIdList.size()) {
			//说明redis中数据有问题 从数据库中查数据并且重新刷新缓存
			return null;
		}
		return messageTextVOList;
	}

	private List<MessageTextVO> getMsgTextVOListByDB(PageQueryDTO dto, Long userId) {
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		return messageTextMapper.selectUserMsgList(userId);
	}
}
