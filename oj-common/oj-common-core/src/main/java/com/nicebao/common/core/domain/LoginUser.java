package com.nicebao.common.core.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * LoginUser
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */

@Getter
@Setter
public class LoginUser {
	//用户昵称
	private String nickName;
	//1  表示普通用户  2 ： 表示管理员用户
	private Integer identity;
	//头像
	private String headImage;
}
