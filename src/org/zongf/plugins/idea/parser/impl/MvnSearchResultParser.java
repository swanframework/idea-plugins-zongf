package org.zongf.plugins.idea.parser.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.zongf.plugins.idea.parser.api.IJSoupParser;
import org.zongf.plugins.idea.vo.SearchResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 解析
 * @author zongf
 * @created 2019-08-10
 * @since 1.0
 */
public class MvnSearchResultParser implements IJSoupParser<List<SearchResult>> {

    // 源日期格式
    private SimpleDateFormat orignalSdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

    // 目标日期格式
    private SimpleDateFormat targetSdf = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);

    @Override
    public List<SearchResult> parser(Element element) {

        List<SearchResult> list = new ArrayList<>();

        // 获取所有的结果div: <div class="im">
        Elements imDivs = element.getElementsByClass("im");

        for (Element imDiv : imDivs) {

            SearchResult searchResult = new SearchResult();

            // 解析标题
            this.parserTitle(imDiv, searchResult);

            // 解析maven坐标:groupId 和 artifactId
            this.parserSubTitle(imDiv, searchResult);

            // 解析描述信息
            this.parseDesc(imDiv, searchResult);

            if (searchResult.getTitle() != null && !"".equals(searchResult.getTitle())){
                list.add(searchResult);
            }
        }


        return list;
    }

    /** 解析标签: <div class="im-description">, 可获得description, lastDate 两个属性
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private void parseDesc(Element imDiv, SearchResult searchResult) {
        Elements descDiv = imDiv.getElementsByClass("im-description");

        // 一个imDiv 中只有一个 im-description 标签
        if (descDiv.size() == 1) {

            String descAndDate = descDiv.get(0).text();

            String[] array = descAndDate.split("Last Release on");

            if (array.length == 2) {
                searchResult.setDescription(array[0]);
                searchResult.setLastDate(convertDate(array[1]));
            }else {
                searchResult.setDescription(descAndDate);
            }
        }
    }

    /** 解析标签<p class="im-subtitle">. 可获得groupId 和 artifactId
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private void parserSubTitle(Element imDiv, SearchResult searchResult) {

        // 获取子标签: <p class="im-subtitle">
        Elements subTitles = imDiv.getElementsByClass("im-subtitle");

        // 一个imDiv 中只有一个 im-subtitle 标签
        if (subTitles.size() == 1) {
            // 获取所有的a 子标签
            Elements aEles = subTitles.get(0).getElementsByTag("a");

            // 如果有两个a标签,则一个是groupId，一个是artifactId
            if (aEles.size() >= 2) {
                searchResult.setGroupId(aEles.get(0).text());
                searchResult.setArtifactId(aEles.get(1).text());
            }
        }
    }

    /** 解析标签 <h2 class="im-title">， 可获得title 和 Useages 两个属性
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private void parserTitle(Element imDiv, SearchResult searchResult) {
        // 获取子标签: <h2 class="im-title">
        Elements subTitles = imDiv.getElementsByClass("im-title");

        // 一个imDiv 中只有一个 im-title 标签
        if (subTitles.size() == 1) {
            // 获取所有的a 子标签
            Elements aEles = subTitles.get(0).getElementsByTag("a");

            // 如果有两个a标签,则一个是title，一个是useages
            if (aEles.size() >= 2) {
                // 设置标题
                searchResult.setTitle(aEles.get(0).text());

                // 解析下载数量
                Elements useages = aEles.get(1).getElementsByTag("b");
                if (useages.size() > 0) {
                    searchResult.setUseages(useages.get(0).text());
                }
            }
        }
    }


    /** 转换日期格式
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private String convertDate(String orginalStr) {
        // 如果为空则返回原字符串
        if (orginalStr == null || "".equals(orginalStr)) {
            return orginalStr;
        }

        try {
            Date date = this.orignalSdf.parse(orginalStr.trim());
            return targetSdf.format(date);
        } catch (ParseException e) {
            // 如果解析出错误, 则返回原字符串
        }

        return orginalStr;
    }
}
