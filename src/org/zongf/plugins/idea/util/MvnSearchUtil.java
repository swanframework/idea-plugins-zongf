package org.zongf.plugins.idea.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.zongf.plugins.idea.cache.MvnVersionResultCache;
import org.zongf.plugins.idea.parser.impl.MvnSearchResultParser;
import org.zongf.plugins.idea.parser.impl.MvnVersionResultParser;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    /** 获取首页列表
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    public static List<SearchResult> queryIndex() {

        // 请求网络，并将响应解析为文档
        Document htmlDocument = getHtmlDocument(URL_BASE);

        // 解析文档
        List<SearchResult> resultList = new MvnSearchResultParser().parser(htmlDocument);

        return resultList;
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
        String url = URL_SEARCH + "?p=" + page + "&q=" + key;

        if (key != null && !"".equals(key.trim())) {
            url = url + "&q=" + key;
        }

        url = url.replaceAll(" ", "+");

        System.out.println(url);

        // 请求网络，并将响应解析为文档
        Document htmlDocument = getHtmlDocument(url);

        // 解析文档
        List<SearchResult> resultList = new MvnSearchResultParser().parser(htmlDocument);

        // 异步加载数据
        loadVersionsAnsy(resultList);

        return resultList;
    }

    /** 默认查询, 查询20条数据
     * @param key 关键字
     * @return List<SearchResult>
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    public static List<SearchResult> search(String key) {

        List<SearchResult> multiList = new ArrayList<>();

        // 查询两页数据
        List<SearchResult> listPage1 = MvnSearchUtil.search(key, 1);
        List<SearchResult> listPage2 = MvnSearchUtil.search(key, 2);
        multiList.addAll(listPage1);
        multiList.addAll(listPage2);
        return multiList;
    }

    /** 查询版本号列表
     * @return List<VersionResult>
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    public static List<VersionResult> queryVersions(String groupId, String artifactId) {

        // 拼接请求地址
        String url = URL_VERSIONS + "/" + groupId + "/" + artifactId;

        // 请求网络，并将响应解析为文档
        Document document = getHtmlDocument(url);

        // 解析文档
        return new MvnVersionResultParser().parser(document);
    }

    /** 异步加载版本号数据 //TODO 优化为多线程
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    public static void loadVersionsAnsy(List<SearchResult> searchResults){
        new Thread(() -> {
            for (SearchResult searchResult : searchResults) {
                List<VersionResult> versionResults = queryVersions(searchResult.getGroupId(), searchResult.getArtifactId());
                String key = searchResult.getGroupId() + ":" + searchResult.getArtifactId();
                MvnVersionResultCache.getInstance().set(key, versionResults);
            }

        }).start();
    }

}
