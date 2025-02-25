package com.nicebao.common.core.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageQueryDTO {
    //每页的数据  必传
    private Integer pageSize = 10;
    //第几页   必传
    private Integer pageNum = 1;
}
