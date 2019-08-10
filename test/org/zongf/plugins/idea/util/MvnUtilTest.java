package org.zongf.plugins.idea.util;

import org.junit.Test;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import java.util.List;


public class MvnUtilTest {


    // 测试搜索包
    @Test
    public void test_search() throws Exception{

        List<SearchResult> results = MvnUtil.search("spring", 1);

        results.forEach(System.out::println);
    }

    // 测试搜索版本号
    @Test
    public void test_version() throws Exception{

        SearchResult searchResult = new SearchResult();
        searchResult.setGroupId("org.springframework");
        searchResult.setArtifactId("spring-beans");

        List<VersionResult> versionResults = MvnUtil.queryVersions(searchResult);
        versionResults.forEach(System.out::println);
    }
}
