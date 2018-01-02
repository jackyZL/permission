package com.zl.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jacky
 * @date 2017/10/26
 */
@Getter
@Setter
@ToString
public class SearchLogParam {

    private Integer type; // LogType

    private String beforeSeg;

    private String afterSeg;

    private String operator;

    private String fromTime;//yyyy-MM-dd HH:mm:ss

    private String toTime;
}
