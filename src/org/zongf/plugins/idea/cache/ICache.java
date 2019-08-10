package org.zongf.plugins.idea.cache;

/** 缓存通用接口
 * @since 1.0
 * @author zongf
 * @created 2019-08-10
 */
public interface ICache<T, R> {


    /** 添加缓存
     * @param key 缓存key
     * @param object 缓存对象
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    void set(T key, R object);


    /** 获取缓存
     * @param key 缓存key
     * @return R 缓存对象
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    R get(T key);

    /** 删除缓存
     * @param key 缓存key
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    default void remove(T key){
        throw new UnsupportedOperationException();
    }

    /** 清空缓存
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    default void clear(){
        throw new UnsupportedOperationException();
    }

}
