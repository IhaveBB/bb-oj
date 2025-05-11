package com.nicebao.friend.service.user;

import com.nicebao.common.core.domain.PageQueryDTO;
import com.nicebao.common.core.domain.TableDataInfo;

/**
 * IUserMessageService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/4
 */
public interface IUserMessageService {
	TableDataInfo list(PageQueryDTO dto);

}
