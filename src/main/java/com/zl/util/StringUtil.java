package com.zl.util;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jacky
 * @date 2017/10/26
 */
public class StringUtil {

    public static List<Integer> splitToListInt(String str) {
        List<String> strList = Splitter.on(",") //以逗号分隔
                .trimResults() //移除前后空格
                .omitEmptyStrings() //移除空字符串
                .splitToList(str);

        // 流遍历方式
        return strList.stream().map(strItem -> Integer.parseInt(strItem)).collect(Collectors.toList());
    }
}
