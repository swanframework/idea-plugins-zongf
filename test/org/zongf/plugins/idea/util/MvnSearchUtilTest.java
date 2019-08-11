package org.zongf.plugins.idea.util;

import org.junit.Test;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import java.util.List;


public class MvnSearchUtilTest {


    // 测试搜索包
    @Test
    public void test_search() throws Exception{

        List<SearchResult> searchResultList = MvnSearchUtil.searchByKey("spring");

        searchResultList.forEach(System.out::println);
    }

    // 测试搜索版本号
    @Test
    public void test_version() throws Exception{
//
        SearchResult searchResult = new SearchResult();
        searchResult.setGroupId("org.springframework");
        searchResult.setArtifactId("spring-beans");

        List<VersionResult> versionResults = MvnSearchUtil.queryVersions(searchResult);
        versionResults.forEach(System.out::println);
    }
}
