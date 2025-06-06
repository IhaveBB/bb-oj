package com.nicebao.system.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nicebao.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * User
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/25
 */

@Getter
@Setter
@TableName("tb_user")
public class User extends BaseEntity {

	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(value = "USER_ID", type = IdType.ASSIGN_ID)
	private Long userId;

	private String nickName;

	private String headImage;

	private Integer sex;

	private String phone;

	private String code;

	private String email;

	private String wechat;

	private String schoolName;

	private String majorName;

	private String introduce;

	private Integer status;
}
