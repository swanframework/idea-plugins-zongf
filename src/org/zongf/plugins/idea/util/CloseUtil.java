package org.zongf.plugins.idea.util;


/** 流关闭工具类
 * @author: zongf
 * @date: 2019-07-01
 */
public class CloseUtil {

    /**手工关闭流
     * @param closeables 任意多个实现AutoCloseable接口的对象
     * @author zongf
     * @date 2019-07-01
     */
    public static void close(AutoCloseable... closeables) {
        if (closeables != null) {
            for (AutoCloseable closeable: closeables) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
