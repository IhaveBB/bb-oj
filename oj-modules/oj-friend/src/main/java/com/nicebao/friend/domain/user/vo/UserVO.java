package com.nicebao.friend.domain.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

/**
 * UserVO
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/28
 */
@Getter
@Setter
public class UserVO {

	@JsonSerialize(using = ToStringSerializer.class)
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
