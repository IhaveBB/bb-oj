package com.nicebao.job.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nicebao.common.core.constants.CacheConstants;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.redis.service.RedisService;
import com.nicebao.job.domain.exam.Exam;
import com.nicebao.job.domain.message.Message;
import com.nicebao.job.domain.message.MessageText;
import com.nicebao.job.domain.message.vo.MessageTextVO;
import com.nicebao.job.domain.user.UserScore;
import com.nicebao.job.mapper.exam.ExamMapper;
import com.nicebao.job.mapper.message.MessageTextMapper;
import com.nicebao.job.mapper.user.UserExamMapper;
import com.nicebao.job.mapper.user.UserSubmitMapper;
import com.nicebao.job.service.IMessageService;
import com.nicebao.job.service.IMessageTextService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ExamXxlJob
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/1
 */
@Component
@Slf4j
public class ExamXxlJob {
	@Autowired
	private ExamMapper examMapper;

	@Autowired
	private RedisService redisService;

	@Autowired
	private IMessageTextService messageTextService;

	@Autowired
	private IMessageService messageService;

	@Autowired
	private UserSubmitMapper userSubmitMapper;

	@Autowired
	private MessageTextMapper messageTextMapper;

	@Autowired
	private UserExamMapper userExamMapper;

	@XxlJob("examListOrganizeHandler")
	public void examListOrganizeHandler() {
		//  统计哪些竞赛应该存入未完赛的列表中  哪些竞赛应该存入历史竞赛列表中   统计出来了之后，再存入对应的缓存中
		log.info("*** examListOrganizeHandler ***");
		List<Exam> unFinishList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
				.select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
				.gt(Exam::getEndTime, LocalDateTime.now())
				.eq(Exam::getStatus, Constants.TRUE)
				.orderByDesc(Exam::getCreateTime));
		refreshCache(unFinishList, CacheConstants.EXAM_UNFINISHED_LIST);

		List<Exam> historyList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
				.select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
				.le(Exam::getEndTime, LocalDateTime.now())
				.eq(Exam::getStatus, Constants.TRUE)
				.orderByDesc(Exam::getCreateTime));

		refreshCache(historyList, CacheConstants.EXAM_HISTORY_LIST);
		log.info("*** examListOrganizeHandler 统计结束 ***");
	}


	@XxlJob("examResultHandler")
	//
	public void examResultHandler() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime minusDateTime = now.minusDays(1);
		List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
				.select(Exam::getExamId, Exam::getTitle)
				.eq(Exam::getStatus, Constants.TRUE)
				.ge(Exam::getEndTime, minusDateTime)
				.le(Exam::getEndTime, now));
		if (CollectionUtil.isEmpty(examList)) {
			return;
		}
		//对竞赛ID列表去重
		Set<Long> examIdSet = examList.stream().map(Exam::getExamId).collect(Collectors.toSet());
		//根据竞赛ID List，去查找出每个竞赛下所有的用户。也就是说把所有竞赛下的用户都放到这一个List里了
		List<UserScore> userScoreList = userSubmitMapper.selectUserScoreList(examIdSet);
		//分组，根据竞赛ID进行分组
		//竞赛1：List<UserScore>
		//竞赛2：List<UserScore>
		Map<Long, List<UserScore>> userScoreMap = userScoreList.stream().collect(Collectors.groupingBy(UserScore::getExamId));
		createMessage(examList, userScoreMap);
	}

	private void createMessage(List<Exam> examList, Map<Long, List<UserScore>> userScoreMap) {
		List<MessageText> messageTextList = new ArrayList<>();
		List<Message> messageList = new ArrayList<>();
		for (Exam exam : examList) {
			Long examId = exam.getExamId();
			//根据这个竞赛ID，去取出对应的Score列表
			List<UserScore> userScoreList = userScoreMap.get(examId);
			int totalUser = userScoreList.size();
			//todo 成绩在哪里排序？
			int examRank = 1;
			for (UserScore userScore : userScoreList) {
				String msgTitle =  exam.getTitle() + "——排名情况";
				String msgContent = "您所参与的竞赛：" + exam.getTitle()
						+ "，本次参与竞赛一共" + totalUser + "人， 您排名第"  + examRank + "名！";
				userScore.setExamRank(examRank);
				MessageText messageText = new MessageText();
				messageText.setMessageTitle(msgTitle);
				messageText.setMessageContent(msgContent);
				messageText.setCreateBy(Constants.SYSTEM_USER_ID);
				messageTextList.add(messageText);
				//message先存基础信息，后续等Messagetext插入后生成id后，再插入外键ID到message表
				Message message = new Message();
				message.setSendId(Constants.SYSTEM_USER_ID);
				message.setCreateBy(Constants.SYSTEM_USER_ID);
				message.setRecId(userScore.getUserId());
				messageList.add(message);
				examRank++;
			}
			userExamMapper.updateUserScoreAndRank(userScoreList);
			redisService.rightPushAll(getExamRankListKey(examId), userScoreList);
		}
		messageTextService.batchInsert(messageTextList);
		Map<String, MessageTextVO> messageTextVOMap = new HashMap<>();
		for (int i = 0; i < messageTextList.size(); i++) {
			MessageText messageText = messageTextList.get(i);
			MessageTextVO messageTextVO = new MessageTextVO();
			BeanUtil.copyProperties(messageText, messageTextVO);
			String msgDetailKey = getMsgDetailKey(messageText.getTextId());
			messageTextVOMap.put(msgDetailKey, messageTextVO);
			Message message = messageList.get(i);
			message.setTextId(messageText.getTextId());
		}
		messageService.batchInsert(messageList);
		//redis 操作
		Map<Long, List<Message>> userMsgMap = messageList.stream().collect(Collectors.groupingBy(Message::getRecId));
		Iterator<Map.Entry<Long, List<Message>>> iterator = userMsgMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Long, List<Message>> entry = iterator.next();
			Long recId = entry.getKey();
			String userMsgListKey = getUserMsgListKey(recId);
			List<Long> userMsgTextIdList = entry.getValue().stream().map(Message::getTextId).toList();
			redisService.rightPushAll(userMsgListKey, userMsgTextIdList);
		}
		redisService.multiSet(messageTextVOMap);
	}


	public void refreshCache(List<Exam> examList, String examListKey) {
		if (CollectionUtil.isEmpty(examList)) {
			return;
		}

		Map<String, Exam> examMap = new HashMap<>();
		List<Long> examIdList = new ArrayList<>();
		for (Exam exam : examList) {
			examMap.put(getDetailKey(exam.getExamId()), exam);
			examIdList.add(exam.getExamId());
		}
		redisService.multiSet(examMap);  //刷新详情缓存
		redisService.deleteObject(examListKey);
		redisService.rightPushAll(examListKey, examIdList);      //刷新列表缓存
	}

	private String getDetailKey(Long examId) {
		return CacheConstants.EXAM_DETAIL + examId;
	}

	private String getUserMsgListKey(Long userId) {
		return CacheConstants.USER_MESSAGE_LIST + userId;
	}

	private String getMsgDetailKey(Long textId) {
		return CacheConstants.MESSAGE_DETAIL + textId;
	}

	private String getExamRankListKey(Long examId) {
		return CacheConstants.EXAM_RANK_LIST + examId;
	}
}
