package com.jky.config;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;

/**
 * lucene测试配置类
 * Created by DT人 on 2017/8/18 17:25.
 */
@Component
public class HelloLucene {

    @Value("${lucene.filepath}")
    private String lucenePath;
    /**
     * 建立索引
     */
    public void index() {
        IndexWriter writer = null;
        try {
            // 1、创建Directory
            Directory directory = new RAMDirectory(); // 创建内存的索引对象
            Directory directory1 = FSDirectory.open(new File(lucenePath + "index01")); // 创建再在硬盘上
            // 2、创建IndexWriter
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
            writer = new IndexWriter(directory1, iwc);
            // 3、创建Document对象
            Document doc = null;
            // 4、为Document添加Field
            File f = new File(lucenePath + "example");
            for (File file : f.listFiles()) {
                doc = new Document();
                doc.add(new Field("content", new FileReader(file)));
                doc.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("path", file.getAbsolutePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                // 5、通过IndexWriter添加文档到索引中
                writer.addDocument(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) { writer.close(); }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 搜索
     */
    public void searcher() {
        try {
            // 1、创建Directory
            Directory directory = FSDirectory.open(new File(lucenePath + "index01"));
            // 2、创建IndexReader
            IndexReader reader = IndexReader.open(directory);
            // 3、根据IndexReader创建IndexSearcher
            IndexSearcher searcher = new IndexSearcher(reader);
            // 4、创建搜索的Query(下面的这两行代码表示搜索内容包含"mysql"的文档)
            // 4.1 创建parser来确定要搜索文件的内容,第二个参数表示搜索的域
            QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
            // 4.2 创建query,表示搜索的域为content中包含mysql的文档
            Query query = parser.parse("com");
            // 5、根据seacher搜索并且返回TopDocs
            TopDocs tds = searcher.search(query, 10);
            // 6、根据TopDoc获取ScoreDoc对象
            ScoreDoc[] sds = tds.scoreDocs;
            for(ScoreDoc sd : sds) {
                // 7、根据seacher和ScoreDoc对象获取具体的Doucument对象
                Document document = searcher.doc(sd.doc);
                // 8、根据Document对象获取
                System.out.println(document.get("filename") + "[" + document.get("path") + "]");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
