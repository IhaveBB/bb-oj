package com.nicebao.friend.domain.question.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDetailVO extends QuestionVO {

    private Long timeLimit;

    private Long spaceLimit;

    private String content;

    private String defaultCode;
}
