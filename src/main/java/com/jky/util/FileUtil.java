package com.jky.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * 文件处理工具类
 * Created by DT人 on 2017/8/22 12:06.
 */
public class FileUtil {

    /**
     * 拷贝指定目录的所有文件到当前目录
     * @param filePath
     * @param fileSufix
     */
    public static void copyFile(String filePath, String fileSufix) {
        try {
            fileSufix = fileSufix.indexOf(".") != 0 ? "." + fileSufix : fileSufix;
            File file = new File(filePath);
            for (File f : file.listFiles()) {
                // 获取文件的路劲
                String destFilName = FilenameUtils.getFullPath(f.getAbsolutePath()) +
                        FilenameUtils.getBaseName(f.getName()) + fileSufix;
                // 拷贝文件
                FileUtils.copyFile(f, new File(destFilName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝指定目录的所有文件到目的文件夹
     * @param filePath 指定目录
     * @param destFilePath 目标目录
     * @param fileSufix 文件后缀名
     */
    public static void copyFile(String filePath, String destFilePath, String fileSufix) {
        try {
            fileSufix = fileSufix.indexOf(".") != 0 ? "." + fileSufix : fileSufix;
            File file = new File(filePath);
            for (File f : file.listFiles()) {
                // 获取文件的路劲
                String destFilName = destFilePath + FilenameUtils.getBaseName(f.getName()) + fileSufix;
                // 拷贝文件
                FileUtils.copyFile(f, new File(destFilName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
