package com.jky.util;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * lucene创建为searcher准备的索引工具类
 *
 * Created by DT人 on 2017/8/18 19:14.
 */
public class IndexUtil2 {

    private String[] ids = {"1", "2", "3", "4", "5", "6"};
    private String[] emails = {"aa@jky.com","bb@jky.com","cc@jky.com","jky1988@qq.com","jky818@163.com","jwj1998@163.com"};
    private String[] content = {
            "welcome to visited myspace,I like you",
            "hello boy, I like basketball",
            "my name is cc, i like cc",
            "welcom to guiyang, i like guiyang",
            "i like basketball",
            "where are you? i like swiming"
    };
        private Date[] dates = {};
    private int[] attachs = {2,3,4,5,4,2};
    private String[] names = {"zhangsan","lisi","john","jetty","make","jack"};

    /**
     * 获取文件的基础路径
     */
    @Value("${lucene.filepath}")
    private String lucenePath;

    public Directory directory = null;

    private Map<String, Float> scores = new HashMap<String, Float>();

    public IndexUtil2(){
        setDate();
        scores.put("jky.com", 2.0f); // 邮箱以结尾jky.com
        scores.put("163.com",1.5f);
        directory = new RAMDirectory(); // 存到内存中
    }


    // 日期的初始化
    private void setDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dates = new Date[ids.length];
        try {
            dates[0] = sdf.parse("2017-07-19");
            dates[1] = sdf.parse("2012-07-19");
            dates[2] = sdf.parse("2017-07-19");
            dates[3] = sdf.parse("2013-07-19");
            dates[4] = sdf.parse("2014-07-19");
            dates[5] = sdf.parse("2016-07-19");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新的方法
     */
    public void update() {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory,
                    new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
            /**
             * Lucene并没有提供更新方法，这里的更新操作其实是如下两个操作的合并
             * 先删除后添加
             */
            Document doc = new Document();
            doc.add(new Field("id", "11", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
            doc.add(new Field("email", emails[0], Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("content", content[0], Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field("name", names[0], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
            writer.updateDocument(new Term("id", "1"), doc);
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
     * 合并索引的方法
     */
    public void merge() {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory,
                    new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
            // 会将索引合并为两段，这两段中的被删除的数据会被清空
            // 特别注意：此处Lucenez在3.5之后不建议使用，因为会消耗大量的开销，Lucene会根据情况自动处理
            writer.forceMerge(2);
            writer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           /* try {
                if(writer != null) { writer.close(); }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }

    /**
     * 强制删除方法
     */
    public void forceDelete() {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory,
                    new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
            writer.forceMergeDeletes();
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
     * 删除方法
     */
    public void delete() {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory,
                    new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
            // 参数是一个选项，可以是一个Query,也可以是一个term，term是一个精确查找的值
            // 此时删除的文档不会被完全的删除，而是存储在一个回收站中，可以恢复
            writer.deleteDocuments(new Term("id","1")); // 删除id为1的文档
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
     * 查询方法
     */
    public void query() {
        IndexReader reader = null;
        try {
            reader = IndexReader.open(directory);
            System.out.println("numDocs:"+reader.numDocs());
            System.out.println("maxDocs:"+reader.maxDoc());
            System.out.println("deletDocs:"+reader.numDeletedDocs());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader != null) { reader.close(); }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            writer.deleteAll(); // 清空之前所有的索引
            Document doc = null;
            for (int i = 0; i < ids.length; i++) {
                doc = new Document(); // 创建文档
                // 下面是添加四个域
                doc.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                doc.add(new Field("email", emails[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("content", content[i], Field.Store.NO, Field.Index.ANALYZED));
                doc.add(new Field("name", names[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                // 存储数字进入doc中
                doc.add(new NumericField("attach", Field.Store.YES, true).setIntValue(attachs[i]));
                // 存储日期
                doc.add(new NumericField("date", Field.Store.YES, true).setLongValue(dates[i].getTime()));
                String et = emails[i].substring(emails[i].lastIndexOf("@")+1);
                System.out.println(et);
                if(scores.containsKey(et)) {
                    doc.setBoost(scores.get(et)); // 设置索引的加权(这种就是百度里面的权重，这样会导致用户体验不好)，值越大权重就越高
                } else {
                    doc.setBoost(0.5f);
                }
                writer.addDocument(doc); // 将域写入当文档中去
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
