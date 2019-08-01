package org.zongf.plugins.idea.util.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Unicode 字符串工具
 * @author: zongf
 * @created: 2019-07-12
 * @since 1.0
 */
public class UnicodeStringUtil {

    /** 将包含Unicode编码的字符串转换为普通字符串
     * @param unicode unicode字符串
     * @return 字符串
     * @since 1.0
     * @author zongf
     * @created 2019-08-01
     */
    public static String unicode2String(String unicode) {
        String unicodeParttenExp = "\\\\u[0-9a-zA-Z]{4}";
        Pattern compile = Pattern.compile("(" + unicodeParttenExp + ")");
        Matcher matcher = compile.matcher(unicode);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            char c = (char) Integer.parseInt(matcher.group().substring(2), 16);
            matcher.appendReplacement(sb, String.valueOf(c));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** 将字符串转换为Unicode编码
     * @param string 字符串
     * @return unicode编码
     * @since 1.0
     * @author zongf
     * @created 2019-08-01
     */
    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

}
