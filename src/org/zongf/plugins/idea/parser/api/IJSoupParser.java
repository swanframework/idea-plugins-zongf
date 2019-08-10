package org.zongf.plugins.idea.parser.api;

import org.jsoup.nodes.Element;

/** Jsoup 解析器
 * @since 1.0
 * @author zongf
 * @created 2019-08-10
 */
public interface IJSoupParser<T> {

    /** 解析文档元素为目标对象
     * @param element 文档元素
     * @return T
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    T parser(Element element);

}
