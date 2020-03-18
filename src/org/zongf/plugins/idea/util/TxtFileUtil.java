package org.zongf.plugins.idea.util;


import org.zongf.plugins.idea.enums.CharsetEnum;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**文本文件读写, 当文件内容过大时, 需要考虑内存
 * @author zongf
 * @date 2019-07-01
 */
public class TxtFileUtil {

    /**读取文件内容, 默认以UTF-8编码打开文件
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @return 文本文件内容
     * @author zongf
     * @date 2019-07-01
     */
    public static List<String> readFile(String filePath){
        return readFile(filePath, line -> false, CharsetEnum.UTF8);
    }

    /**读取文件内容, 以指定编码格式打开文件
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param encodeCharset 文件编码
     * @return List<String> 文件内容
     * @author zongf
     * @date 2019-07-01
     */
    public static List<String> readFile(String filePath, CharsetEnum encodeCharset){
        return readFile(filePath, line -> false, encodeCharset);
    }

    /**读取文件内容, 可忽略空行
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param ignoreEmptyLine 忽略空行
     * @return List<String> 文件内容
     * @author zongf
     * @date 2019-07-01
     */
    public static List<String> readFile(String filePath, boolean ignoreEmptyLine){
        return readFile(filePath, line -> "".equals(line.trim()), CharsetEnum.UTF8);
    }

    /**读取文件内容, 以指定编码格式打开文件, 可忽略空行
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param ignoreEmptyLine 忽略空行
     * @return List<String> 文件内容
     * @author zongf
     * @date 2019-07-01
     */
    public static List<String> readFile(String filePath, boolean ignoreEmptyLine, CharsetEnum encodeCharset){
        return readFile(filePath, line -> "".equals(line.trim()), encodeCharset);
    }

    /**读取文件内容, 可自定义过滤器, 过滤行. 默认以UTF-8编码打开文件
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param filter 过滤器
     * @return List<String> 文件内容
     * @author zongf
     * @date 2019-07-01
     */
    public static List<String> readFile(String filePath, Predicate<String> filter){
        return readFile(filePath, filter, CharsetEnum.UTF8);
    }

    /**读取文件内容.
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param encodeCharset 设置以哪种文件编码打开文件
     * @param ignoreFilter 忽略的行过滤器
     * @return List<String> 文件内容
     * @author zongf
     * @date 2019-07-01
     */
    public static List<String> readFile(String filePath, Predicate<String> ignoreFilter, CharsetEnum encodeCharset) {

        // 创建list 存储文件内容
        List<String> contents = new ArrayList<String>();

        File file = new File(filePath);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encodeCharset.toString()));

            String line = null;

            while ((line = br.readLine()) != null) {
                if (!ignoreFilter.test(line)) {
                    contents.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(filePath + "文件不存在", e);
        } catch (IOException e) {
            throw new RuntimeException(filePath + "文件打开失败", e);
        }finally {
            CloseUtil.close(br);
        }
        return contents;
    }

    /**向文件中追加内容. 默认以UTF-8编码写入文件. 当文件已存在时, 进行文件覆盖
     * @param contents 文件内容
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @author zongf
     * @date 2019-07-01
     */
    public static void writeFile(List<String> contents, String filePath){
        writeFile(contents, filePath, CharsetEnum.UTF8, true);
    }

    /**向文件中追加内容, 默认以UTF-8编码写入文件
     * @param contents 文件内容
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param overrideFile 当文件已存在时, 是否进行文件覆盖.
     * @author zongf
     * @date 2019-07-01
     */
    public static void writeFile(List<String> contents, String filePath, boolean overrideFile){
        writeFile(contents, filePath, CharsetEnum.UTF8, overrideFile);
    }

    /**向文件中追加内容, 当文件存在时, 会进行文件覆盖
     * @param contents 文件内容
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param encodeCharset 指定新成文件文件编码
     * @author zongf
     * @date 2019-07-01
     */
    public static void writeFile(List<String> contents, String filePath, CharsetEnum encodeCharset){
        writeFile(contents, filePath, encodeCharset, true);
    }

    /**向文件中追加内容.
     * @param contents 文件内容
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param encodeCharset 指定新成文件文件编码
     * @param overrideFile 当文件已存在时, 是否进行文件覆盖
     * @author zongf
     * @date 2019-07-01
     */
    public static void writeFile(List<String> contents, String filePath, CharsetEnum encodeCharset, boolean overrideFile) {

        File file = new File(filePath);

        // 校验文件是否存在
        if(file.exists() && !overrideFile){
            throw new RuntimeException("文件已存在!");
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encodeCharset.toString()));
            for (String content : contents) {
                bw.write(content);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            CloseUtil.close(bw);
        }
    }

    /**向文件中追加内容, 默认以UTF-8格式写入文件, 当文件不存在时创建新的文件.
     * @param contents 文件内容
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @author zongf
     * @date 2019-07-01
     */
    public static void appendFile(List<String> contents, String filePath){
        appendFile(contents, filePath, CharsetEnum.UTF8, true);
    }

    /**向文件中追加内容, 默认写入文件编码为UTF-8
     * @param contents 文件内容
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param creatNewFile 当文件不存在时, 是否创建新的文件
     * @author zongf
     * @date 2019-07-01
     */
    public static void appendFile(List<String> contents, String filePath, boolean creatNewFile){
        appendFile(contents, filePath, CharsetEnum.UTF8, creatNewFile);
    }

    /**向文件中追加内容, 当文件不存在时, 创建新文件
     * @param contents 文件内容
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param encodeCharset 指定新成文件文件编码
     * @author zongf
     * @date 2019-07-01
     */
    public static void appendFile(List<String> contents, String filePath, CharsetEnum encodeCharset){
        appendFile(contents, filePath, encodeCharset, true);
    }

    /**向文件中追加内容.
     * @param contents 文件内容
     * @param filePath 文件路径, 支持相对路径和绝对路径
     * @param encodeCharset 指定新成文件文件编码
     * @param creatNewFile 当文件不存在时, 是否创建新的文件
     * @author zongf
     * @date 2019-07-01
     */
    public static void appendFile(List<String> contents, String filePath, CharsetEnum encodeCharset, boolean creatNewFile) {

        File file = new File(filePath);

        // 校验文件是否存在
        if(!file.exists() && !creatNewFile){
            throw new RuntimeException("文件不存在!");
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), encodeCharset.toString()));
            for (String content : contents) {
                bw.write(content);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            CloseUtil.close(bw);
        }
    }

}