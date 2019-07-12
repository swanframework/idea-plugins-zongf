package org.zongf.plugins.idea.util.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: zongf
 * @created: 2019-07-12
 * @since 1.0
 */
public class ClassUtil {

    /** 简化class名称为简单名称, 如java.util.List 简化为List
     * @param className java类名称
     * @return 类简单名称
     * @since 1.0
     * @author zongf
     * @created 2019-07-12
     */
    public static String simpleClassName(String className) {

        // 字符串为空, 则原样返回
        if(className == null || "".equals(className.trim())) return className;

        Pattern pattern = Pattern.compile("([a-zA-Z0-9]*(?:\\.[a-zA-Z0-9]*)+)");
        Matcher matcher = pattern.matcher(className);
        while (matcher.find()) {
            // 获取匹配到的字符串
            String name = matcher.group();
            // 简化字符串
            String simpleName = StringUtil.subStringAfterLast(name, ".");
            // 将匹配到的字符串替换
            className = className.replace(name, simpleName);
        }
        return className;
    }

}
