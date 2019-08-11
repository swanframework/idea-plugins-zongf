package org.zongf.plugins.idea.util.common;

/** 新增字符串工具类
 * @author: zongf
 * @created: 2019-07-11
 * @since 1.0
 */
public class StringFormatUtil {


    /** 隐藏超过长度的字符串， 用... 替换
     * @param str 原字符串
     * @param maxLength 最大长度，包含...
     * @return String
     * @since 1.0
     * @author zongf
     * @created 2019-08-11
     */
    public static String hideOverride(String str, int maxLength){
        return hideOverride(str, maxLength, "...");
    }

    /** 隐藏超过长度的字符串
     * @param str 原字符串
     * @param maxLength 最大长度，包含替换字符串的长度
     * @param replace 超出长度需要替换的字符串
     * @return String
     * @since 1.0
     * @author zongf
     * @created 2019-08-11
     */
    public static String hideOverride(String str, int maxLength, String replace) {

        // 如果字符串为空, 则返回原字符串
        if(StringUtil.isEmpty(str)) return str;

        // 如果替换字符串长度大于总长度，则不进行替换
        if(replace.length() > maxLength) return str;

        if (str.length() > maxLength) {
            String subStr = str.substring(0, maxLength - replace.length());
            str = subStr + replace;
        }

        return str;
    }
}
