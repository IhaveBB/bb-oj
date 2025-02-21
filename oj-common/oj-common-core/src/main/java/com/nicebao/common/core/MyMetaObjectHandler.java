package com.nicebao.common.core;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.nicebao.common.core.constants.Constants;
import com.nicebao.common.core.utils.ThreadLocalUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyMetaObjectHandler
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/21
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

	/**
	 *  插入时自动填充 create_time 和 create_by
	 * @author IhavBB
	 * @date 18:28 2025/2/21
	 * @param metaObject
	**/
	@Override
	public void insertFill(MetaObject metaObject) {
		this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
		this.strictInsertFill(metaObject, "createBy", Long.class, ThreadLocalUtil.get(Constants.USER_ID, Long.class));
	}

	/**
	 * 更新自动填入操作人和时间
	 * @author IhavBB
	 * @date 18:27 2025/2/21
	 * @param metaObject
	**/
	@Override
	public void updateFill(MetaObject metaObject) {
		this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
		this.strictUpdateFill(metaObject, "updateBy", Long.class, ThreadLocalUtil.get(Constants.USER_ID, Long.class));

	}
}