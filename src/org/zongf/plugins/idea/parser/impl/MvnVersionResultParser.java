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

            // 获取所有的td
            Elements trs = tbody.getElementsByTag("tr");

            for (Element tr : trs) {
                // 获取tbody 元素中的<td>
                Elements tds = tr.getElementsByTag("td");

                int size = tds.size();

                VersionResult versionResult = new VersionResult();

                // td 有四列的，有五列的，五列的第一列是主版本号
                versionResult.setVersion(tds.get(size-4).text());
                versionResult.setRepository(tds.get(size-3).text());
                versionResult.setUsages(tds.get(size-2).text());
                versionResult.setPublishDate(this.convertDate(tds.get(size-1).text()));

                // 当显示不全时
                if (versionResult.getVersion().contains("...")) {
                    fixedFullVersion(tds.get(size - 4), versionResult);
                }

                versionList.add(versionResult);
            }

        }
        return versionList;
    }


    /** 当版本号显示不全时，补全版本号
     * @param versionTd 版本号Td 元素
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private void fixedFullVersion(Element versionTd, VersionResult versionResult) {
        // 获取a标签
        Elements versionAs = versionTd.getElementsByTag("a");

        if (versionAs.size() > 0) {
            // 获取a标签的链接地址
            String href = versionAs.get(0).attr("href");

            // 如果a标签不为空
            if (href != null) {
                String[] split = href.split("/");
                if (split.length == 2) {
                    versionResult.setVersion(split[1]);
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
