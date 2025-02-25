package com.nicebao.system.domain.sysuser;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nicebao.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * SysUser
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/18
 */
@TableName("tb_sys_user")
@Data
public class SysUser extends BaseEntity {
	// 用户id（主键） 使用雪花算法
	@TableId(value = "USER_ID", type = IdType.ASSIGN_ID)
	private Long userId;

	// 用户账号（唯一）
	private String userAccount;

	// 用户密码
	private String password;

	// 昵称
	private String nickName;
}
