package org.zongf.plugins.idea.util.common;

/** 新增字符串工具类
 * @author: zongf
 * @created: 2019-07-11
 * @since 1.0
 */
public class StringUtil {

    /** 截取第一个separator字符串之前的字符串, 如果separator不存在, 则返回空字符串
     * @param str 原字符串
     * @param separator 分隔字符串
     * @return 空字符串或separator之前的字符串
     * @since 1.0
     * @author zongf
     * @created 2019-07-11 
     */
    public static String subStringBeforeFirst(String str, String separator) {
        // 如果字符串为空, 则原字符串返回
        if(str == null || "".equals(str)) return str;

        int idx = str.indexOf(separator);
        return idx == -1 ? "" : str.substring(0, idx);
    }

    /** 截取最后一个separator字符串之前的字符串, 如果separator不存在, 则返回空字符串
     * @param str 原字符串
     * @param separator 分隔字符串
     * @return 空字符串或separator之前的字符串
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    public static String subStringBeforeLast(String str, String separator) {
        // 如果字符串为空, 则原字符串返回
        if(str == null || "".equals(str)) return str;

        int idx = str.lastIndexOf(separator);
        return idx == -1 ? "" : str.substring(0, idx);
    }

    /** 截取第一个separator字符串之后的字符串, 如果separator不存在, 则返回空字符串
     * @param str 原字符串
     * @param separator 分隔字符串
     * @return 空字符串或separator之前的字符串
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    public static String subStringAfterFirst(String str, String separator) {

        // 如果字符串为空, 则原字符串返回
        if(str == null || "".equals(str)) return str;

        int idx = str.indexOf(separator);
        return idx == -1 ? "" : str.substring(idx + 1);
    }

    /** 截取最后一个separator字符串之后的字符串, 如果separator不存在, 则返回空字符串
     * @param str 原字符串
     * @param separator 分隔字符串
     * @return 空字符串或separator之前的字符串
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    public static String subStringAfterLast(String str, String separator) {
        // 如果字符串为空, 则原字符串返回
        if(str == null || "".equals(str)) return str;

        int idx = str.lastIndexOf(separator);
        return idx == -1 ? "" : str.substring(idx + 1);
    }

    /** 截取两个分割符字符串之间的字符串, 如果任意分隔字符串不存在, 则返回空字符串
     * @param str 原字符串
     * @param open 开始字符串
     * @param close 结束字符串
     * @return 空字符串或separator之前的字符串
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    public static String subStringBetween(String str, String open, String close) {
        // 如果字符串为空, 则返回原字符串
        if(str == null || "".equals(str)) return str;

        int beginIdx = str.indexOf(open);
        int endIdx = str.indexOf(close);

        return beginIdx != -1 && endIdx != -1 ? str.substring(beginIdx + 1, endIdx) : "";
    }

    /** 字符串首字母大写
     * @param str 原字符串
     * @return 首字母大写的字符串
     * @since 1.0
     * @author zongf
     * @created 2019-07-12
     */
    public static String firstToUpper(String str) {
        // 如果字符串为空, 则返回原字符串
        if(str == null || "".equals(str)) return str;

        String first = str.substring(0, 1);
        return first.toUpperCase() + str.substring(1);
    }

    /** 字符串是否以某字符串开头
     * @param str 字符串
     * @param prefix 前缀
     * @return null
     * @since 1.0
     * @author zongf
     * @created 2019-07-17
     */
    public static boolean isBeforeWith(String str, String prefix) {

        // 如果字符串为空, 则返回原字符串
        if(prefix == null || str == null || "".equals(str) || "".equals(prefix)) return false;
        int i = str.indexOf(prefix);

        return i == 0;
    }

    /** 字符串是否以某字符串结尾
     * @param str 字符串
     * @param suffix 后缀
     * @return null
     * @since 1.0
     * @author zongf
     * @created 2019-07-17
     */
    public static boolean isEndWith(String str, String suffix) {
        // 如果字符串为空, 则返回原字符串
        if(suffix == null || str == null || "".equals(str) || "".equals(suffix)) return false;

        int id = str.lastIndexOf(suffix);
        return id == str.length() - suffix.length();
    }

    /** 判断字符串是否为空
     * @param str
     * @return null或一连串空格均视为空
     * @since 1.0
     * @author zongf
     * @created 2019-07-17
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

}
