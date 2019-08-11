package org.zongf.plugins.idea.cache;

import org.zongf.plugins.idea.util.MvnSearchUtil;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** maven 版本号缓存
 * @since 1.0
 * @author zongf
 * @created 2019-08-11
 */
public class MvnVersionResultCache implements ICache<SearchResult, List<VersionResult>> {

    private Map<String,  List<VersionResult>> dataMap = new HashMap<>();

    public static final MvnVersionResultCache VERSION_RESULT_CACHE = new MvnVersionResultCache();

    private MvnVersionResultCache() {
    }

    public static MvnVersionResultCache getInstance() {
        return VERSION_RESULT_CACHE;
    }

    @Override
    public void set(SearchResult searchResult,  List<VersionResult> versionResultList) {
        String key = getKey(searchResult);
        dataMap.put(key, versionResultList);
    }

    /** 尝试从缓存中获取, 如果缓存中没有，则进行查询
     * @since 1.0
     * @author zongf
     * @created 2019-08-11
     */
    @Override
    public  List<VersionResult> get(SearchResult searchResult) {
        String key = getKey(searchResult);

        // 尝试从缓存中获取
        List<VersionResult> resultList = dataMap.get(key);

        // 如果缓存结果为空, 则发送查询
        if (resultList == null) {
            resultList = MvnSearchUtil.queryVersions(searchResult);
            dataMap.put(key, resultList);
        }
        return resultList;
    }

    /** 获取缓存中的key
     * @param searchResult 查询结果
     * @return String key 格式为 groupId:artifactId
     * @since 1.0
     * @author zongf
     * @created 2019-08-11
     */
    private String getKey(SearchResult searchResult) {
        return searchResult.getGroupId() + ":" + searchResult.getArtifactId();
    }

}
