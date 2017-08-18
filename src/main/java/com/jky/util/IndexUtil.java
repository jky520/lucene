package com.jky.util;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * lucene创建索引的工具类
 *
 * Created by DT人 on 2017/8/18 19:14.
 */
@Component
public class IndexUtil {

    private String[] ids = {"1", "2", "3", "4", "5", "6"};
    private String[] emails = {"aa@jky.com","bb@jky.com","cc@jky.com","jky1988@qq.com","jky818@163.com","jwj1998@163.com"};
    private String[] content = {
            "welcome to visited myspace",
            "hello boy",
            "my name is cc",
            "welcom to guiyang",
            "i like basketball",
            "where are you?"
    };
    private int[] attachs = {2,3,4,5,4,2};
    private String[] names = {"zhangsan","lisi","john","jetty","make","jack"};

    private static Directory directory = null;

    // 静态块创建Directory对象
    static {
        try {
            directory = FSDirectory.open(new File("E:/lucene/index02"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 索引方法
     */
    public void index() {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory,
                    new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
            Document doc = null;
            for (int i = 0; i < ids.length; i++) {
                doc = new Document();
                doc.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                doc.add(new Field("email", emails[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("content", content[i], Field.Store.NO, Field.Index.ANALYZED));
                doc.add(new Field("name", names[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
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
}
