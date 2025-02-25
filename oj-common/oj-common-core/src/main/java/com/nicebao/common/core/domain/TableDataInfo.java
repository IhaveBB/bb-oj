package com.nicebao.common.core.domain;

/**
 * TableDataInfo
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/24
 */

import com.nicebao.common.core.enums.ResultCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 表格分页数据对象
 * @author IhavBB
 * @date 22:26 2025/2/24
 * @param null
 * @return {@link null}
**/
@Getter
@Setter
public class TableDataInfo {

	//总记录数
	private long total;
	//列表数据
	private List<?> rows;
	//消息状态码
	private int code;
	//消息内容
	private String msg;
	//表格数据对象
	public TableDataInfo() {
	}

	//未查出任何数据时调用
	public static TableDataInfo empty() {
		TableDataInfo rspData = new TableDataInfo();
		rspData.setCode(ResultCode.SUCCESS.getCode());
		rspData.setRows(new ArrayList<>());
		rspData.setMsg(ResultCode.SUCCESS.getMsg());
		rspData.setTotal(0);
		return rspData;
	}

	//查出数据时调用
	public static TableDataInfo success(List<?> list,long total) {
		TableDataInfo rspData = new TableDataInfo();
		rspData.setCode(ResultCode.SUCCESS.getCode());
		rspData.setRows(list);
		rspData.setMsg(ResultCode.SUCCESS.getMsg());
		rspData.setTotal(total);
		return rspData;
	}
}
