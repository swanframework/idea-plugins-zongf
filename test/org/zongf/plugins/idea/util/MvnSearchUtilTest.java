package org.zongf.plugins.idea.util;

import org.junit.Test;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import java.util.List;


public class MvnSearchUtilTest {


    // 测试搜索包
    @Test
    public void test_search() throws Exception{

        List<SearchResult> results = MvnSearchUtil.search("spring", 1);

        results.forEach(System.out::println);
    }

    // 测试搜索版本号
    @Test
    public void test_version() throws Exception{

        List<VersionResult> versionResults = MvnSearchUtil.queryVersions("org.springframework", "spring-beans");
        versionResults.forEach(System.out::println);
    }
}
