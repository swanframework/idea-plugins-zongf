package org.zongf.plugins.idea.cache;

import org.zongf.plugins.idea.vo.VersionResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-10 17:26
 */
public class MvnVersionResultCache implements ICache<String, List<VersionResult>> {

    private Map<String,  List<VersionResult>> dataMap = new HashMap<>();

    public static final MvnVersionResultCache VERSION_RESULT_CACHE = new MvnVersionResultCache();

    private MvnVersionResultCache() {
    }

    public static MvnVersionResultCache getInstance() {
        return VERSION_RESULT_CACHE;
    }

    @Override
    public void set(String key,  List<VersionResult> versionResultList) {
        dataMap.put(key, versionResultList);
    }

    @Override
    public  List<VersionResult> get(String key) {
        return dataMap.get(key);
    }

    @Override
    public void clear() {
        dataMap.clear();
    }

    @Override
    public void remove(String key) {
        dataMap.remove(key);
    }

}
