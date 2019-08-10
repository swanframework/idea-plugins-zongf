package org.zongf.plugins.idea.parser.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.zongf.plugins.idea.parser.api.IJSoupParser;
import org.zongf.plugins.idea.vo.VersionResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** 解析版本号结果
 * @since 1.0
 * @author zongf
 * @created 2019-08-10
 */
public class MvnVersionResultParser implements IJSoupParser<List<VersionResult>> {

    // 源日期格式
    private SimpleDateFormat orignalSdf = new SimpleDateFormat("MMM,yyyy", Locale.ENGLISH);

    // 目标日期格式
    private SimpleDateFormat targetSdf = new SimpleDateFormat("yyyy.MM", Locale.ENGLISH);

    @Override
    public List<VersionResult> parser(Element element) {

        List<VersionResult> versionList = new ArrayList<VersionResult>();

        // 获取包裹版本表格的div: <div class="gridcontainer">
        Element tableDiv = element.getElementsByClass("gridcontainer").get(0);

        // 获取表格中所有的tbody
        Elements tbodys = tableDiv.getElementsByTag("tbody");

        // 遍历tbody
        for (Element tbody : tbodys) {

            // 获取tbody 元素中的<td>
            Elements tds = tbody.getElementsByTag("td");

            if (tds.size() >= 5) {
                VersionResult versionResult = new VersionResult();

                // 解析值
                versionResult.setMainVersion(tds.get(0).text());
                versionResult.setVersion(tds.get(1).text());
                versionResult.setRepository(tds.get(2).text());
                versionResult.setUsages(tds.get(3).text());
                versionResult.setPublishDate(this.convertDate(tds.get(4).text()));

                versionList.add(versionResult);
            }
        }
        return versionList;
    }

    /** 转换日期格式
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private String convertDate(String orginalStr) {
        // 如果为空则返回原字符串
        if (orginalStr == null || "".equals(orginalStr.trim())) {
            return orginalStr;
        }

        try {
            Date date = this.orignalSdf.parse(orginalStr);
            return targetSdf.format(date);
        } catch (ParseException e) {
            // 如果解析出错误, 则返回原字符串
        }

        return orginalStr;
    }
}
