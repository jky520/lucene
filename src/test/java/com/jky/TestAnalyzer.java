package com.jky;

import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
import com.jky.analyzer.MySameAnalyzer;
import com.jky.analyzer.MyStopAnalyzer;
import com.jky.intf.impl.SimpleSameWordContent;
import com.jky.util.AnalyzerUtil;
import com.jky.util.MMsegUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

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
        System.out.println(MMsegUtil.WORDS_BASE_PATH);
        // 后面方的是分词库文件的文件目录（中文分词器）
        Analyzer a5 = new MMSegAnalyzer(new File(MMsegUtil.WORDS_BASE_PATH));
        String txt = "我来自中国贵阳南明区";
        String txt1 = "京华时报２００８年1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
        AnalyzerUtil.displayToken(txt, a1);
        AnalyzerUtil.displayToken(txt, a2);
        AnalyzerUtil.displayToken(txt, a3);
        AnalyzerUtil.displayToken(txt, a4);
        AnalyzerUtil.displayToken(txt, a5);
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

    @Test
    public void test05() {
        try {
            Analyzer a1 = new MySameAnalyzer(new SimpleSameWordContent());
            String txt = "我来自中国贵州贵阳南明区师专";
            Directory dict = new RAMDirectory();
            IndexWriter writer = new IndexWriter(dict, new IndexWriterConfig(Version.LUCENE_35, a1));
            Document doc = new Document();
            doc.add(new Field("content", txt, Field.Store.YES, Field.Index.ANALYZED));
            writer.addDocument(doc);
            writer.close();
            IndexSearcher searcher = new IndexSearcher(IndexReader.open(dict));
            TopDocs tds = searcher.search(new TermQuery(new Term("content","天朝")), 10);
            Document d = searcher.doc(tds.scoreDocs[0].doc);
            System.out.println(d.get("content"));
            //AnalyzerUtil.displayAllTokenInfo(txt, a1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
