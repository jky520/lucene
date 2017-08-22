package com.jky.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.StringReader;

/**
 * 分词工具类
 * Created by DT人 on 2017/8/22 20:47.
 */
public class AnalyzerUtil {

    /**
     *
     * @param str
     * @param analyzer
     */
    public static void displayToken(String str, Analyzer analyzer) {
        try {
            TokenStream stream = analyzer.tokenStream("content", new StringReader(str));
            // 创建一个属性，这个属性会添加到流中，随着这个TokenStream增加
            CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
            while (stream.incrementToken()) {
                System.out.print("["+cta+"]");
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param str
     * @param analyzer
     */
    public static void displayAllTokenInfo(String str, Analyzer analyzer) {
        try {
            TokenStream stream = analyzer.tokenStream("content", new StringReader(str));
            // 位置增量的属性，存储语汇单元之间的距离
            PositionIncrementAttribute pia = stream.addAttribute(PositionIncrementAttribute.class);
            // 每个语汇单元的位置偏移量
            OffsetAttribute oa = stream.addAttribute(OffsetAttribute.class);
            // 存储每一个语汇单元的信息（也就是分词单元的信息）
            CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
            // 使用的分词的类型的信息
            TypeAttribute ta = stream.addAttribute(TypeAttribute.class);
            for (; stream.incrementToken() ;) {
                System.out.print(pia.getPositionIncrement() + ":");
                System.out.print(cta + "[" + oa.startOffset() + "-" + oa.endOffset() +"]-->"+ta.type()+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
