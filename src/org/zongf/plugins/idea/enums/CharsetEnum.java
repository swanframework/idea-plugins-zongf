package org.zongf.plugins.idea.enums;

/** 字符集编码
 * @author zongf
 * @date 2019-12-04
 */
public enum CharsetEnum {

    UTF8("UTF-8"),
    GBK("GBK"),
    GB2312("GB2312"),
    ISO88591("ISO-8859-1");

    private String charset;

    CharsetEnum(String charset){
        this.charset = charset;
    }

    @Override
    public String toString() {
        return this.charset;
    }
}
