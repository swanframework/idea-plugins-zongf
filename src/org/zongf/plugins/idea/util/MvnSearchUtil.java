package org.zongf.plugins.idea.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.zongf.plugins.idea.parser.impl.MvnSearchResultParser;
import org.zongf.plugins.idea.parser.impl.MvnVersionResultParser;
import org.zongf.plugins.idea.util.common.MultiThreadUtil;
import org.zongf.plugins.idea.util.common.StringUtil;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-09 20:09
 */
public class MvnSearchUtil {

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
    private static Document getHtmlDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 12000);
        } catch (IOException e) {
            throw new RuntimeException("网络错误", e);
        } catch (Exception e) {
            throw new RuntimeException("获取网络文档错误!", e);
        }
        return document;
    }

    /** 获取搜索url地址
     * @param key 关键字
     * @param page 页数
     * @return String
     * @since 1.0
     * @author zongf
     * @created 2019-08-11
     */
    private static String getSearchUrl(String key, int page) {

        // 如果没有查询关键字, 则直接查询首页
        if(StringUtil.isEmpty(key)) return URL_BASE;

        // 拼接请求地址
        String url = URL_SEARCH + "?p=" + page + "&q=" + key;

        // 替换所有空格为+ 号
        return url.replaceAll(" ", "+");
    }

    /** 默认查询, 查询20条数据
     * @param url 查询地址
     * @return List<SearchResult>
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private static List<SearchResult> searchByUrl(String url) {

        // 请求网络，并将响应解析为文档
        Document htmlDocument = getHtmlDocument(url);

        // 解析文档
        List<SearchResult> searchResultList = new MvnSearchResultParser().parser(htmlDocument);

        // 异步加载数据
//        MultiThreadUtil.execute(MvnSearchUtil::queryVersions, searchResultList);

        return searchResultList;
    }

    public static List<SearchResult> searchByKey(String key) {

        // 查询地址
        List<String> urlList = new ArrayList<>();
        urlList.add(getSearchUrl(key, 1));

        // 如果key不为空, 则查询2页数据
        if (!StringUtil.isEmpty(key)) {
            urlList.add(getSearchUrl(key, 2));
        }

        // 多线程执行两次查询
        List<List<SearchResult>> resultList = MultiThreadUtil.callable(MvnSearchUtil::searchByUrl, urlList);

        // 合并结果
        List<SearchResult> multiList = resultList.stream().flatMap(List::stream).collect(Collectors.toList());

        return multiList;
    }

    /** 查询版本号列表
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
}
