package org.zongf.plugins.idea.util;

/** 字符串工具类
 * @author zongf
 * @date 2020-01-07
 */
public class NameUtil {

    /** 驼峰命名变量
     * @param str
     * @return String
     * @author zongf
     * @date 2020-01-07
     */
    public static String camelCase(String str) {

        if (str == null || "".equals(str.trim())) {
            return null;
        }

        // 分割字符串
        String[] array = str.split("_");

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            String item = array[i];
            if (item.length() > 1) {
                sb.append(item.substring(0, 1).toUpperCase()).append(item.substring(1));
            }else {
                sb.append(item.toUpperCase());
            }
        }
        return sb.toString();

    }

    /** 匈牙利命名
     * @param name 驼峰命名
     * @param contactChar 连接符
     * @return String
     * @author zongf
     * @date 2020-03-17
     */
    public static String hungarian(String name, char contactChar){
        StringBuilder sb=new StringBuilder(name);
        int temp=0;//定位
        for(int i=0;i<name.length();i++){
            if(Character.isUpperCase(name.charAt(i))){
                sb.insert(i+temp, contactChar);
                temp+=1;
            }
        }
        return sb.toString().toLowerCase();
    }

    /** 首字母小写
     * @param name
     * @return String
     * @author zongf
     * @date 2020-03-17
     */
    public static String firstLowerCase(String name) {
        if (Character.isLowerCase(name.charAt(0))) {
            return name;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(name.charAt(0))).append(name.substring(1)).toString();
        }
    }


}
