package com.jky.util;

import com.chenlb.mmseg4j.*;
import com.jky.mmseg.data.MMSeg4j;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * mmseg4j
 * 中文分词器工具类
 * Created by DT人 on 2017/8/23 9:36.
 */
public class MMsegUtil {

    // 获取分词库文件的根地址
    public static final String WORDS_BASE_PATH = MMSeg4j.class.getResource("").getPath().replaceFirst("/","").replace("target/classes", "src/main/java").replaceAll("/", "\\\\").replaceAll("%20"," ");

    /*protected static Dictionary dic;

    public MMsegUtil() {
        dic = Dictionary.getInstance();
    }

    protected static Seg getSeg() {
        return new ComplexSeg(dic);
    }

    public static String segWords(Reader input, String wordSpilt) throws IOException {
        StringBuilder sb = new StringBuilder();
        Seg seg = getSeg();	//取得不同的分词具体算法
        MMSeg mmSeg = new MMSeg(input, seg);
        Word word = null;
        boolean first = true;
        while((word=mmSeg.next())!=null) {
            if(!first) {
                sb.append(wordSpilt);
            }
            String w = word.getString();
            sb.append(w);
            first = false;

        }
        return sb.toString();
    }

    public static String segWords(String txt, String wordSpilt){
        try {
            return segWords(new StringReader(txt), wordSpilt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
