package org.zongf.plugins.idea.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.zongf.plugins.idea.parser.impl.MvnSearchResultParser;
import org.zongf.plugins.idea.parser.impl.MvnVersionResultParser;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-09 20:09
 */
public class MvnUtil {

    // 网站地址
    private static final String URL_BASE = "https://mvnrepository.com";

    // 模糊搜索
    // https://mvnrepository.com/search?q=spring&p=2
    public static final String URL_SEARCH = URL_BASE + "/search";

    // 版本列表
    // https://mvnrepository.com/artifact/org.springframework/spring-context
    public static final String URL_VERSIONS = URL_BASE + "/artifact";


    /** 获取html文档
     * @param url 网址
     * @return Document html 文档
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    public static Document getHtmlDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 3000);
        } catch (IOException e) {
            throw new RuntimeException("网络错误", e);
        } catch (Exception e) {
            throw new RuntimeException("获取网络文档错误!", e);
        }
        return document;
    }


    /** 查询版本号列表
     * @param searchResult
     * @return List<VersionResult>
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    public static List<VersionResult> queryVersions(SearchResult searchResult) {

        // 拼接请求地址
        String url = URL_VERSIONS + "/" + searchResult.getGroupId() + "/" + searchResult.getArtifactId();

        // 请求网络，并将响应解析为文档
        Document document = getHtmlDocument(url);

        // 解析文档
        return new MvnVersionResultParser().parser(document);
    }


    /** 模糊搜索
     * @param key 关键字
     * @param page 页码
     * @return List<SearchResult>
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    public static List<SearchResult> search(String key, int page) {

        // 拼接请求地址
        String url = URL_SEARCH + "?q='" + key + "'&p=" + page;

        // 请求网络，并将响应解析为文档
        Document htmlDocument = getHtmlDocument(url);

        // 解析文档
        return new MvnSearchResultParser().parser(htmlDocument);
    }

}
