package com.jky;

import com.jky.analyzer.MyStopAnalyzer;
import com.jky.util.AnalyzerUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by DT人 on 2017/8/22 21:01.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAnalyzer {
    /**
     * 查看各种分词器的分词原理
     */
    @Test
    public void test01() {
        Analyzer a1 = new StandardAnalyzer(Version.LUCENE_35);
        Analyzer a2 = new StopAnalyzer(Version.LUCENE_35);
        Analyzer a3 = new SimpleAnalyzer(Version.LUCENE_35);
        Analyzer a4 = new WhitespaceAnalyzer(Version.LUCENE_35);
        String txt = "this is my house,I am come from guizhou kaili,My email is jky1988@qq.com" +
                ",My QQ is 1132882670";
        AnalyzerUtil.displayToken(txt, a1);
        AnalyzerUtil.displayToken(txt, a2);
        AnalyzerUtil.displayToken(txt, a3);
        AnalyzerUtil.displayToken(txt, a4);
    }

    @Test
    public void test02() {
        Analyzer a1 = new StandardAnalyzer(Version.LUCENE_35);
        Analyzer a2 = new StopAnalyzer(Version.LUCENE_35);
        Analyzer a3 = new SimpleAnalyzer(Version.LUCENE_35);
        Analyzer a4 = new WhitespaceAnalyzer(Version.LUCENE_35);
        String txt = "我来自中国贵阳南明区华信智原";
        AnalyzerUtil.displayToken(txt, a1);
        AnalyzerUtil.displayToken(txt, a2);
        AnalyzerUtil.displayToken(txt, a3);
        AnalyzerUtil.displayToken(txt, a4);
    }

    @Test
    public void test03() {
        Analyzer a1 = new StandardAnalyzer(Version.LUCENE_35);
        Analyzer a2 = new StopAnalyzer(Version.LUCENE_35);
        Analyzer a3 = new SimpleAnalyzer(Version.LUCENE_35);
        Analyzer a4 = new WhitespaceAnalyzer(Version.LUCENE_35);
        String txt = "how are you thank you";
        AnalyzerUtil.displayAllTokenInfo(txt, a1);
        System.out.println("--------------------------------");
        AnalyzerUtil.displayAllTokenInfo(txt, a2);
        System.out.println("--------------------------------");
        AnalyzerUtil.displayAllTokenInfo(txt, a3);
        System.out.println("--------------------------------");
        AnalyzerUtil.displayAllTokenInfo(txt, a4);
    }

    @Test
    public void test04() {
        Analyzer a1 = new MyStopAnalyzer(new String[]{"I","you", "hate"});
        Analyzer a2 = new MyStopAnalyzer();
        String txt = "how are you thank you I hate you";
        AnalyzerUtil.displayToken(txt, a1);
        AnalyzerUtil.displayToken(txt, a2);
    }
}
