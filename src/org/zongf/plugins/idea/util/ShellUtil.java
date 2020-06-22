package org.zongf.plugins.idea.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

/** shell命令执行工具
 * @author zongf
 * @date 2020-06-22
 */
public class ShellUtil {

    /** 执行多条shell命令,并返回最后一条命令的结果
     * @param shells 多条命令
     * @return List<String>
     * @author zongf
     * @date 2020-06-22
     */
    public static List<String> runShell(List<String> shells) {
        StringBuffer commandSb = new StringBuffer();
        shells.forEach(command -> commandSb.append(command).append(" && "));
        commandSb.delete(commandSb.length()-3, commandSb.length());
        return runShell(commandSb.toString());
    }

    /** 执行单条shell 命令
     * @param shell shell 命令
     * @return List<String> 返回结果
     * @author zongf
     * @date 2020-06-22
     */
    public static List<String> runShell(String shell) {
        List<String> strList = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",shell},null,null);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            process.waitFor();
            while ((line = input.readLine()) != null){
                strList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strList;
    }

    public static void main(String[] args) {
        String absPath = "/workspace/mybatis/mybatis-3";
        List<String> shells = new ArrayList<>();
        shells.add("cd " + absPath);
        shells.add("git remote -v");
        List<String> results = ShellUtil.runShell(shells);
        results.forEach(System.out::println);
    }
}
